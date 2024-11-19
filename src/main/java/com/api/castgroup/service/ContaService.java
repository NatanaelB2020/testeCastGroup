package com.api.castgroup.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.castgroup.dto.ContaDTO;
import com.api.castgroup.dto.OperacaoDTO;

@Service
public interface ContaService {

    ContaDTO criarConta(ContaDTO contaDTO);

    ContaDTO creditar(OperacaoDTO operacaoDTO);

    ContaDTO debitar(OperacaoDTO operacaoDTO);

    void transferir(OperacaoDTO operacaoDTO);

    List<ContaDTO> listarContas();
}
