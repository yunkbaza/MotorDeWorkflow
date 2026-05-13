package com.baeza.workflow_engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(ApiKeyFilter apiKeyFilter) {
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return new org.springframework.security.provisioning.InMemoryUserDetailsManager();
    }

    @Bean // 🌟 O SEGREDO ESTAVA AQUI! Sem isto, o Spring ignora este método.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Desativar CSRF (necessário para APIs REST)
                .csrf(AbstractHttpConfigurer::disable)

                // Definir que a API não guarda estado de sessão
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Permitir todas as rotas (porque o nosso ApiKeyFilter fará o controlo manual)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                // Injetar o nosso filtro de API Key antes do filtro padrão do Spring
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}