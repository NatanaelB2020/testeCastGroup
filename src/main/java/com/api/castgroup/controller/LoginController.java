package com.api.castgroup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Rota para a página de login
    @GetMapping("/login")
    public String login() {
        return "login"; // Nome do arquivo Thymeleaf (login.html)
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("error", "Credenciais inválidas. Tente novamente.");
        return "login"; // Retorna para a página de login em caso de erro
    }

}
