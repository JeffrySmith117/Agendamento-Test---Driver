package com.jeffry.agendamento.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "veiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String modelo;

    @NotBlank
    private String versao;

    private String placa; // null quando for veiculo de teste da concessionaria

    @Column(nullable = false)
    private boolean disponivelParaTestDrive = true;

    public Veiculo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getVersao() { return versao; }
    public void setVersao(String versao) { this.versao = versao; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public boolean isDisponivelParaTestDrive() { return disponivelParaTestDrive; }
    public void setDisponivelParaTestDrive(boolean disponivelParaTestDrive) { this.disponivelParaTestDrive = disponivelParaTestDrive; }
}
