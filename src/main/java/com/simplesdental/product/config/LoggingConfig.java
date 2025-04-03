package com.simplesdental.product.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class LoggingConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Adiciona ID único para cada requisição
            MDC.put("requestId", UUID.randomUUID().toString());
            
            // Adiciona ID da sessão se disponível
            String sessionId = request.getSession(false) != null ? request.getSession().getId() : "";
            MDC.put("sessionId", sessionId);
            
            // Adiciona ID do usuário se autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                MDC.put("userId", auth.getName());
            }
            
            filterChain.doFilter(request, response);
        } finally {
            // Limpa o MDC após a requisição
            MDC.clear();
        }
    }
}
