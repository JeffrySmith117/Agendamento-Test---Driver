package com.jeffry.agendamento.controller;

import com.jeffry.agendamento.dto.AgendamentoDtos.*;
import com.jeffry.agendamento.model.Usuario;
import com.jeffry.agendamento.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public AgendamentoResponse criar(@AuthenticationPrincipal Usuario cliente,
                                      @Valid @RequestBody NovoAgendamentoRequest req) {
        return agendamentoService.criar(cliente, req);
    }

    @GetMapping("/meus")
    public List<AgendamentoResponse> meusAgendamentos(@AuthenticationPrincipal Usuario cliente) {
        return agendamentoService.listarPorCliente(cliente.getId());
    }

    // usado pelo painel admin para ver a agenda do dia/periodo
    // restrito a ADMIN: sem essa checagem, qualquer cliente autenticado
    // conseguiria listar os agendamentos e nomes de outros clientes
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AgendamentoResponse> listarPorPeriodo(@RequestParam LocalDateTime inicio,
                                                        @RequestParam LocalDateTime fim) {
        return agendamentoService.listarPorPeriodo(inicio, fim);
    }

    // "diferencial IA": sugere o proximo horario livre a partir da preferencia do cliente
    @GetMapping("/sugestao")
    public HorarioSugeridoResponse sugerirHorario(@RequestParam Long veiculoId,
                                                   @RequestParam LocalDateTime preferencia) {
        return agendamentoService.sugerirHorario(veiculoId, preferencia);
    }
}