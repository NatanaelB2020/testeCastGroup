package com.api.castgroup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.api.castgroup.dto.ContaDTO;
import com.api.castgroup.dto.OperacaoDTO;
import com.api.castgroup.entity.ContaEntity;
import com.api.castgroup.repository.ContaRepository;
import com.api.castgroup.service.ContaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContasController2 {

    @Qualifier("contaServiceImpl")
    private final ContaService contaService;

    private final ContaRepository contaRepository;

    @GetMapping("/admin/criar-conta")
    public String criarContaForm(Model model) {
        model.addAttribute("contaEntity", new ContaEntity());
        return "admin-criar-conta"; // nome do template Thymeleaf
    }

    @PostMapping("/admin/criar-conta")
    public String criarConta(@ModelAttribute ContaEntity contaEntity) {
        // Converte a ContaEntity para um ContaDTO
        ContaDTO contaDTO = new ContaDTO(contaEntity.getNumeroConta(), contaEntity.getSaldo());

        // Usa o ContaService para criar a conta
        contaService.criarConta(contaDTO);

        // Redireciona para a página de listagem de contas
        return "redirect:/contas/admin/listar";
    }

    @GetMapping("/admin/listar")
    public String listarContas(Model model) {
        // Obtém a lista de contas como DTOs
        List<ContaDTO> contas = contaService.listarContas();

        // Adiciona a lista de contas ao modelo
        model.addAttribute("contas", contas);

        // Retorna a página de listagem de contas
        return "listar_contas"; // Página Thymeleaf
    }

    @PostMapping("/usuario/creditar")
    public String creditar(@RequestBody OperacaoDTO operacaoDTO, Model model) {
        contaService.creditar(operacaoDTO);
        return "redirect:/contas/usuario";
    }

    @PostMapping("/usuario/debitar")
    public String debitar(@RequestBody OperacaoDTO operacaoDTO, Model model) {
        contaService.debitar(operacaoDTO);
        return "redirect:/contas/usuario";
    }

    @PostMapping("/usuario/transferir")
    public String transferir(@RequestBody OperacaoDTO operacaoDTO, Model model) {
        contaService.transferir(operacaoDTO);
        return "redirect:/contas/usuario";
    }
}
