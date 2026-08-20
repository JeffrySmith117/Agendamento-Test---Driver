package com.jeffry.agendamento.dto;

import com.jeffry.agendamento.model.Agendamento;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AgendamentoDtos {

    public record NovoAgendamentoRequest(
        @NotNull Long veiculoId,
        @NotNull LocalDateTime horario,
        @NotNull Agendamento.TipoServico tipoServico,
        String observacoes
    ) {}

    public record AgendamentoResponse(
        Long id,
        String clienteNome,
        String veiculoModelo,
        LocalDateTime horario,
        Agendamento.TipoServico tipoServico,
        Agendamento.StatusAgendamento status
    ) {
        public static AgendamentoResponse from(Agendamento a) {
            return new AgendamentoResponse(
                a.getId(),
                a.getCliente().getNome(),
                a.getVeiculo().getModelo() + " " + a.getVeiculo().getVersao(),
                a.getHorario(),
                a.getTipoServico(),
                a.getStatus()
            );
        }
    }

    // usado pelo endpoint de "horarios disponiveis" que sugere o proximo slot livre
    public record HorarioSugeridoResponse(LocalDateTime horarioSugerido, String motivo) {}
}
