package com.simplesdental.product.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            logger.debug("Authorization header recebido: [{}]", authHeader);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.debug("Header de autorização inválido ou ausente: [{}]", authHeader);
                filterChain.doFilter(request, response);
                return;
            }

            try {
                final String jwt = authHeader.substring(7);
                logger.debug("Token JWT extraído: [{}]", jwt);
                logger.debug("Número de partes do token: {}", jwt.split("\\.").length);
                
                final String userEmail = jwtService.extractUsername(jwt);
                logger.debug("Email extraído do token: [{}]", userEmail);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    logger.debug("Loaded UserDetails for email: {}", userEmail);
                    
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                        logger.debug("Authentication set in SecurityContext for user: {}", userEmail);
                    } else {
                        logger.warn("Invalid JWT token for user: {}", userEmail);
                    }
                } else {
                    logger.debug("SecurityContext already contains authentication or userEmail is null. UserEmail: {}, Authentication: {}", 
                        userEmail, SecurityContextHolder.getContext().getAuthentication());
                }
            } catch (IllegalArgumentException e) {
                logger.error("Erro ao processar token JWT: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token JWT inválido");
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage(), e);
            throw e;
        }
    }
}
