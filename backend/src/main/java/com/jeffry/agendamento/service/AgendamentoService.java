package com.jeffry.agendamento.service;

import com.jeffry.agendamento.dto.AgendamentoDtos.*;
import com.jeffry.agendamento.model.Agendamento;
import com.jeffry.agendamento.model.Usuario;
import com.jeffry.agendamento.model.Veiculo;
import com.jeffry.agendamento.repository.AgendamentoRepository;
import com.jeffry.agendamento.repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AgendamentoService {

    private static final LocalTime ABERTURA = LocalTime.of(8, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(18, 0);
    private static final int DURACAO_SLOT_MINUTOS = 60;

    private final AgendamentoRepository agendamentoRepository;
    private final VeiculoRepository veiculoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository, VeiculoRepository veiculoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public AgendamentoResponse criar(Usuario cliente, NovoAgendamentoRequest req) {
        Veiculo veiculo = veiculoRepository.findById(req.veiculoId())
            .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado."));

        validarHorarioComercial(req.horario());

        if (agendamentoRepository.existsByVeiculoIdAndHorario(veiculo.getId(), req.horario())) {
            throw new IllegalStateException("Esse horário já está reservado para este veículo. Tente outro horário.");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setVeiculo(veiculo);
        agendamento.setHorario(req.horario());
        agendamento.setTipoServico(req.tipoServico());
        agendamento.setObservacoes(req.observacoes());

        return AgendamentoResponse.from(agendamentoRepository.save(agendamento));
    }

    public List<AgendamentoResponse> listarPorCliente(Long clienteId) {
        return agendamentoRepository.findByClienteId(clienteId).stream()
            .map(AgendamentoResponse::from)
            .toList();
    }

    public List<AgendamentoResponse> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return agendamentoRepository.findByHorarioBetween(inicio, fim).stream()
            .map(AgendamentoResponse::from)
            .toList();
    }

    /**
     * "Diferencial IA": sugere o proximo horario livre para o veiculo pedido,
     * a partir da preferencia do cliente. Hoje e uma heuristica simples
     * (varre slots de hora em hora dentro do expediente); o ponto de extensao
     * fica pronto para plugar um modelo de recomendacao/LLM no futuro.
     */
    public HorarioSugeridoResponse sugerirHorario(Long veiculoId, LocalDateTime preferencia) {
        LocalDateTime candidato = ajustarParaExpediente(preferencia);

        for (int i = 0; i < 20; i++) { // procura nos proximos 20 slots uteis
            if (!agendamentoRepository.existsByVeiculoIdAndHorario(veiculoId, candidato)) {
                String motivo = candidato.equals(preferencia)
                    ? "Horário solicitado está disponível."
                    : "Horário solicitado indisponível; sugerindo o slot livre mais próximo.";
                return new HorarioSugeridoResponse(candidato, motivo);
            }
            candidato = proximoSlot(candidato);
        }

        throw new IllegalStateException("Nenhum horário disponível encontrado nos próximos dias.");
    }

    private void validarHorarioComercial(LocalDateTime horario) {
        DayOfWeek dia = horario.getDayOfWeek();
        LocalTime hora = horario.toLocalTime();

        if (dia == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Não atendemos aos domingos.");
        }
        if (hora.isBefore(ABERTURA) || hora.isAfter(FECHAMENTO.minusHours(1))) {
            throw new IllegalArgumentException("Horário fora do expediente (08h às 18h).");
        }
        if (hora.getMinute() != 0) {
            throw new IllegalArgumentException("Agendamentos devem começar em horas cheias.");
        }
    }

    private LocalDateTime ajustarParaExpediente(LocalDateTime dt) {
        LocalDateTime ajustado = dt.withMinute(0).withSecond(0).withNano(0);
        if (ajustado.toLocalTime().isBefore(ABERTURA)) {
            ajustado = ajustado.with(ABERTURA);
        } else if (ajustado.toLocalTime().isAfter(FECHAMENTO.minusHours(1))) {
            ajustado = ajustado.plusDays(1).with(ABERTURA);
        }
        if (ajustado.getDayOfWeek() == DayOfWeek.SUNDAY) {
            ajustado = ajustado.plusDays(1).with(ABERTURA);
        }
        return ajustado;
    }

    private LocalDateTime proximoSlot(LocalDateTime atual) {
        LocalDateTime proximo = atual.plusMinutes(DURACAO_SLOT_MINUTOS);
        return ajustarParaExpediente(proximo);
    }
}
