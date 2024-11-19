package com.api.castgroup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperacaoDTO {
    private String numeroContaOrigem;
    private String numeroContaDestino;
    private BigDecimal valor;
}
