package com.baeza.workflow_engine.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${workflow.api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestKey = request.getHeader("X-API-KEY");

        // 🔍 LOG DE DIAGNÓSTICO (Aparecerá no terminal do IntelliJ)
        System.out.println("🛡️ Segurança | Chave Esperada: [" + apiKey + "] | Chave Recebida: [" + requestKey + "]");

        // Usamos .trim() para evitar erros com espaços invisíveis e verificamos se não é nulo
        if (requestKey != null && apiKey.trim().equals(requestKey.trim())) {
            filterChain.doFilter(request, response);
        } else {
            System.err.println("❌ Bloqueado: API Key não coincide!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Content-Type", "text/plain;charset=UTF-8");
            response.getWriter().write("Acesso Negado: API Key invalida ou ausente.");
        }
    }
}