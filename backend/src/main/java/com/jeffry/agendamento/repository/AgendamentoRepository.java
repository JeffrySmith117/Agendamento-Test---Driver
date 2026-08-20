package com.jeffry.agendamento.repository;

import com.jeffry.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteId(Long clienteId);

    List<Agendamento> findByHorarioBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByVeiculoIdAndHorario(Long veiculoId, LocalDateTime horario);
}
