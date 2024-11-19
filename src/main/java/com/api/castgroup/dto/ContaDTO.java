package com.api.castgroup.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaDTO {
    private Long id;
    private String numeroConta;
    private BigDecimal saldo;

    public ContaDTO(String numeroConta, BigDecimal saldo) {
        this.numeroConta = numeroConta;
        this.saldo = saldo;
    }
}