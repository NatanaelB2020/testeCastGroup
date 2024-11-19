package com.api.castgroup.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.api.castgroup.dto.ContaDTO;
import com.api.castgroup.dto.OperacaoDTO;
import com.api.castgroup.entity.ContaEntity;
import com.api.castgroup.exception.InsufficientFundsException;
import com.api.castgroup.repository.ContaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;

    @Override
    @Transactional
    public ContaDTO criarConta(ContaDTO contaDTO) {
        // Cria uma nova ContaEntity e copia os dados do DTO
        ContaEntity contaEntity = new ContaEntity();
        contaEntity.setNumeroConta(contaDTO.getNumeroConta());
        contaEntity.setSaldo(contaDTO.getSaldo());

        // Salva a ContaEntity no banco de dados
        ContaEntity savedEntity = contaRepository.save(contaEntity);

        // Retorna o ContaDTO com os dados da ContaEntity salva
        return new ContaDTO(savedEntity.getId(), savedEntity.getNumeroConta(), savedEntity.getSaldo());
    }

    @Override
    @Transactional
    public ContaDTO creditar(OperacaoDTO operacaoDTO) {
        ContaEntity conta = contaRepository.findByNumeroConta(operacaoDTO.getNumeroContaDestino())
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));
        conta.setSaldo(conta.getSaldo().add(operacaoDTO.getValor()));
        contaRepository.save(conta);
        return new ContaDTO(conta.getId(), conta.getNumeroConta(), conta.getSaldo());
    }

    @Override
    @Transactional
    public ContaDTO debitar(OperacaoDTO operacaoDTO) {
        ContaEntity conta = contaRepository.findByNumeroConta(operacaoDTO.getNumeroContaOrigem())
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));

        if (conta.getSaldo().compareTo(operacaoDTO.getValor()) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente para a operação de débito");
        }

        conta.setSaldo(conta.getSaldo().subtract(operacaoDTO.getValor()));
        contaRepository.save(conta);
        return new ContaDTO(conta.getId(), conta.getNumeroConta(), conta.getSaldo());
    }

    @Override
    @Transactional
    public void transferir(OperacaoDTO operacaoDTO) {
        debitar(new OperacaoDTO(operacaoDTO.getNumeroContaOrigem(), null, operacaoDTO.getValor()));
        creditar(new OperacaoDTO(null, operacaoDTO.getNumeroContaDestino(), operacaoDTO.getValor()));
    }

    @Override
    public List<ContaDTO> listarContas() {
        return contaRepository.findAll().stream()
                .map(conta -> new ContaDTO(conta.getId(), conta.getNumeroConta(), conta.getSaldo()))
                .collect(Collectors.toList());
    }
}
