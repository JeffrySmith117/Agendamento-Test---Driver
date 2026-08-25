package com.jeffry.agendamento.service;

import com.jeffry.agendamento.dto.AgendamentoDtos.*;
import com.jeffry.agendamento.model.Agendamento;
import com.jeffry.agendamento.model.Usuario;
import com.jeffry.agendamento.model.Veiculo;
import com.jeffry.agendamento.repository.AgendamentoRepository;
import com.jeffry.agendamento.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        cliente = new Usuario();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        veiculo = new Veiculo();
        veiculo.setId(10L);
        veiculo.setModelo("Honda City");
        veiculo.setVersao("EX CVT");
    }

    @Nested
    @DisplayName("Criacao de agendamento")
    class CriarAgendamento {

        @Test
        @DisplayName("cria agendamento com sucesso dentro do horario comercial e sem conflito")
        void criaComSucesso() {
            LocalDateTime horario = proximaSegundaAs(10);
            NovoAgendamentoRequest req = new NovoAgendamentoRequest(
                veiculo.getId(), horario, Agendamento.TipoServico.TEST_DRIVE, null
            );

            when(veiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));
            when(agendamentoRepository.existsByVeiculoIdAndHorario(veiculo.getId(), horario)).thenReturn(false);
            when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(invocation -> {
                Agendamento a = invocation.getArgument(0);
                a.setId(100L);
                a.setStatus(Agendamento.StatusAgendamento.CONFIRMADO);
                return a;
            });

            AgendamentoResponse resposta = agendamentoService.criar(cliente, req);

            assertThat(resposta.id()).isEqualTo(100L);
            assertThat(resposta.status()).isEqualTo(Agendamento.StatusAgendamento.CONFIRMADO);
            verify(agendamentoRepository).save(any(Agendamento.class));
        }

        @Test
        @DisplayName("rejeita agendamento quando o veiculo/horario ja esta reservado (anti-overbooking)")
        void rejeitaConflitoDeHorario() {
            LocalDateTime horario = proximaSegundaAs(10);
            NovoAgendamentoRequest req = new NovoAgendamentoRequest(
                veiculo.getId(), horario, Agendamento.TipoServico.TEST_DRIVE, null
            );

            when(veiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));
            when(agendamentoRepository.existsByVeiculoIdAndHorario(veiculo.getId(), horario)).thenReturn(true);

            assertThatThrownBy(() -> agendamentoService.criar(cliente, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ja esta reservado");

            verify(agendamentoRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejeita agendamento aos domingos")
        void rejeitaDomingo() {
            LocalDateTime domingo = proximoDomingoAs(10);
            NovoAgendamentoRequest req = new NovoAgendamentoRequest(
                veiculo.getId(), domingo, Agendamento.TipoServico.REVISAO, null
            );

            when(veiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));

            assertThatThrownBy(() -> agendamentoService.criar(cliente, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("domingos");
        }

        @Test
        @DisplayName("rejeita agendamento fora do horario comercial (antes das 8h)")
        void rejeitaForaDoExpediente() {
            LocalDateTime madrugada = proximaSegundaAs(6);
            NovoAgendamentoRequest req = new NovoAgendamentoRequest(
                veiculo.getId(), madrugada, Agendamento.TipoServico.TEST_DRIVE, null
            );

            when(veiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));

            assertThatThrownBy(() -> agendamentoService.criar(cliente, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expediente");
        }

        @Test
        @DisplayName("rejeita horario que nao seja em hora cheia")
        void rejeitaHorarioQuebrado() {
            LocalDateTime horarioQuebrado = proximaSegundaAs(10).plusMinutes(30);
            NovoAgendamentoRequest req = new NovoAgendamentoRequest(
                veiculo.getId(), horarioQuebrado, Agendamento.TipoServico.TEST_DRIVE, null
            );

            when(veiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));

            assertThatThrownBy(() -> agendamentoService.criar(cliente, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("horas cheias");
        }

        @Test
        @DisplayName("rejeita agendamento para veiculo inexistente")
        void rejeitaVeiculoInexistente() {
            NovoAgendamentoRequest req = new NovoAgendamentoRequest(
                999L, proximaSegundaAs(10), Agendamento.TipoServico.TEST_DRIVE, null
            );

            when(veiculoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> agendamentoService.criar(cliente, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nao encontrado");
        }
    }

    @Nested
    @DisplayName("Sugestao de horario (diferencial IA)")
    class SugestaoDeHorario {

        @Test
        @DisplayName("sugere o proprio horario pedido quando ele esta livre")
        void sugereHorarioPedidoQuandoLivre() {
            LocalDateTime preferencia = proximaSegundaAs(10);
            when(agendamentoRepository.existsByVeiculoIdAndHorario(anyLong(), any())).thenReturn(false);

            HorarioSugeridoResponse resposta = agendamentoService.sugerirHorario(veiculo.getId(), preferencia);

            assertThat(resposta.horarioSugerido()).isEqualTo(preferencia);
            assertThat(resposta.motivo()).contains("disponivel");
        }

        @Test
        @DisplayName("sugere o proximo slot livre quando o horario pedido esta ocupado")
        void sugereProximoSlotQuandoOcupado() {
            LocalDateTime preferencia = proximaSegundaAs(10);
            LocalDateTime proximoSlot = preferencia.plusHours(1);

            when(agendamentoRepository.existsByVeiculoIdAndHorario(veiculo.getId(), preferencia)).thenReturn(true);
            when(agendamentoRepository.existsByVeiculoIdAndHorario(veiculo.getId(), proximoSlot)).thenReturn(false);

            HorarioSugeridoResponse resposta = agendamentoService.sugerirHorario(veiculo.getId(), preferencia);

            assertThat(resposta.horarioSugerido()).isEqualTo(proximoSlot);
            assertThat(resposta.motivo()).contains("indisponivel");
        }
    }

    private LocalDateTime proximaSegundaAs(int hora) {
        LocalDateTime data = LocalDateTime.now().plusWeeks(1);
        while (data.getDayOfWeek().getValue() != 1) {
            data = data.plusDays(1);
        }
        return data.withHour(hora).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalDateTime proximoDomingoAs(int hora) {
        LocalDateTime data = LocalDateTime.now().plusWeeks(1);
        while (data.getDayOfWeek().getValue() != 7) {
            data = data.plusDays(1);
        }
        return data.withHour(hora).withMinute(0).withSecond(0).withNano(0);
    }
}
