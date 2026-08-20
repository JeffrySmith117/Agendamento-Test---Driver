package com.jeffry.agendamento.controller;

import com.jeffry.agendamento.model.Veiculo;
import com.jeffry.agendamento.repository.VeiculoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoRepository veiculoRepository;

    public VeiculoController(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @GetMapping("/disponiveis")
    public List<Veiculo> disponiveis() {
        return veiculoRepository.findByDisponivelParaTestDriveTrue();
    }
}
