package com.api.castgroup.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.Authentication;

@Configuration
@EnableWebSecurity
public class SecurityUserConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(new AntPathRequestMatcher("/login"), new AntPathRequestMatcher("/home"))
                        .permitAll() // Permite login sem autenticação
                        .requestMatchers(new AntPathRequestMatcher("/contas/admin/**")).hasRole("ADMIN") // Restringe
                                                                                                         // para ADMIN
                        .requestMatchers(new AntPathRequestMatcher("/contas/usuario/**")).hasRole("USER") // Restringe
                                                                                                          // para USER
                        .anyRequest().authenticated() // Exige autenticação para outras páginas
                )
                .formLogin(form -> form
                        .loginPage("/login") // Página personalizada de login
                        .permitAll() // Permite acesso à página de login para todos
                        .successHandler(successHandler()) // Configura o handler de sucesso de login
                )
                .logout(logout -> logout
                        .permitAll() // Permite logout para todos
                );
        return http.build();
    }

    // Definindo o handler para o redirecionamento após login
    private AuthenticationSuccessHandler successHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            // Verificando os papéis do usuário
            if (authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
                // Redireciona para a página de administração
                response.sendRedirect("/contas/admin/criar-conta");
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"))) {
                // Redireciona para a página de usuário
                response.sendRedirect("/contas/usuario/operacoes");
            } else {
                // Caso o usuário não tenha um papel válido, pode redirecionar para a página
                // inicial ou de erro
                response.sendRedirect("/home");
            }
        };
    }
}
