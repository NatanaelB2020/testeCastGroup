package com.api.castgroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.api.castgroup.dto.ContaDTO;
import com.api.castgroup.dto.OperacaoDTO;
import com.api.castgroup.entity.ContaEntity;
import com.api.castgroup.exception.InsufficientFundsException;
import com.api.castgroup.repository.ContaRepository;
import com.api.castgroup.service.ContaServiceImpl;

public class ContaServiceImplTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaServiceImpl contaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarConta() {
        // Arrange
        ContaDTO contaDTO = new ContaDTO(null, "12345", BigDecimal.valueOf(1000));
        ContaEntity contaEntity = new ContaEntity(null, "12345", BigDecimal.valueOf(1000));
        ContaEntity contaSalva = new ContaEntity(1L, "12345", BigDecimal.valueOf(1000));

        when(contaRepository.save(any(ContaEntity.class))).thenReturn(contaSalva);

        // Act
        ContaDTO resultado = contaService.criarConta(contaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("12345", resultado.getNumeroConta());
        verify(contaRepository, times(1)).save(any(ContaEntity.class));
    }

    @Test
    void deveCreditarValorNaConta() {
        // Arrange
        OperacaoDTO operacaoDTO = new OperacaoDTO(null, "12345", BigDecimal.valueOf(200));
        ContaEntity contaEntity = new ContaEntity(1L, "12345", BigDecimal.valueOf(1000));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(contaEntity));

        // Act
        ContaDTO resultado = contaService.creditar(operacaoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(1200), resultado.getSaldo());
        verify(contaRepository, times(1)).save(contaEntity);
    }

    @Test
    void deveDebitarValorDaContaComSaldoSuficiente() {
        // Arrange
        OperacaoDTO operacaoDTO = new OperacaoDTO("12345", null, BigDecimal.valueOf(200));
        ContaEntity contaEntity = new ContaEntity(1L, "12345", BigDecimal.valueOf(1000));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(contaEntity));

        // Act
        ContaDTO resultado = contaService.debitar(operacaoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(800), resultado.getSaldo());
        verify(contaRepository, times(1)).save(contaEntity);
    }

    @Test
    void deveLancarExcecaoAoDebitarValorMaiorQueOSaldo() {
        // Arrange
        OperacaoDTO operacaoDTO = new OperacaoDTO("12345", null, BigDecimal.valueOf(2000));
        ContaEntity contaEntity = new ContaEntity(1L, "12345", BigDecimal.valueOf(1000));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(contaEntity));

        // Act & Assert
        InsufficientFundsException excecao = assertThrows(
                InsufficientFundsException.class,
                () -> contaService.debitar(operacaoDTO));
        assertEquals("Saldo insuficiente para a operação de débito", excecao.getMessage());
        verify(contaRepository, never()).save(any(ContaEntity.class));
    }

    @Test
    void deveTransferirValorEntreContas() {
        // Arrange
        OperacaoDTO operacaoDTO = new OperacaoDTO("12345", "67890", BigDecimal.valueOf(300));
        ContaEntity contaOrigem = new ContaEntity(1L, "12345", BigDecimal.valueOf(1000));
        ContaEntity contaDestino = new ContaEntity(2L, "67890", BigDecimal.valueOf(500));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByNumeroConta("67890")).thenReturn(Optional.of(contaDestino));
        when(contaRepository.save(any(ContaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        contaService.transferir(operacaoDTO);

        // Assert
        assertEquals(BigDecimal.valueOf(700), contaOrigem.getSaldo());
        assertEquals(BigDecimal.valueOf(800), contaDestino.getSaldo());
        verify(contaRepository, times(2)).save(any(ContaEntity.class));
    }

    @Test
    void deveListarTodasAsContas() {
        // Arrange
        List<ContaEntity> contas = Arrays.asList(
                new ContaEntity(1L, "12345", BigDecimal.valueOf(1000)),
                new ContaEntity(2L, "67890", BigDecimal.valueOf(2000)));

        when(contaRepository.findAll()).thenReturn(contas);

        // Act
        List<ContaDTO> resultado = contaService.listarContas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("12345", resultado.get(0).getNumeroConta());
        assertEquals(BigDecimal.valueOf(2000), resultado.get(1).getSaldo());
        verify(contaRepository, times(1)).findAll();
    }
}
