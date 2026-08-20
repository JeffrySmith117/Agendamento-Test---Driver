package com.jeffry.agendamento.repository;

import com.jeffry.agendamento.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByDisponivelParaTestDriveTrue();
}
