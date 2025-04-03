package com.simplesdental.product.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        try {
            logger.debug("Tentando extrair username do token");
            String username = extractClaim(token, Claims::getSubject);
            logger.debug("Username extraído do token: {}", username);
            if (username == null) {
                logger.error("Username extraído do token é null");
                throw new IllegalArgumentException("Token não contém username");
            }
            return username;
        } catch (Exception e) {
            logger.error("Erro ao extrair username do token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Erro ao processar token JWT", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("Erro ao extrair claims do token: {}", e.getMessage());
            return null;
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        try {
            logger.debug("Gerando token JWT para usuário: {}", userDetails.getUsername());
            String token = Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.debug("Token JWT gerado com sucesso: {}", token);
            return token;
        } catch (Exception e) {
            logger.error("Erro ao gerar token JWT: {}", e.getMessage(), e);
            throw new IllegalStateException("Erro ao gerar token JWT", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            logger.debug("Validando token para usuário: {}", username);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Erro ao validar token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            logger.debug("Extraindo claims do token JWT usando secret-key: {}", secretKey.substring(0, 10) + "...");
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Erro ao extrair claims do token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Erro ao processar token JWT: " + e.getMessage(), e);
        }
    }

    private Key getSignInKey() {
        try {
            logger.debug("Decodificando secret-key para gerar chave de assinatura");
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            logger.debug("Secret-key decodificada com sucesso, gerando chave HMAC-SHA256");
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Erro ao gerar chave de assinatura: {}", e.getMessage(), e);
            throw new IllegalStateException("Erro ao gerar chave de assinatura JWT", e);
        }
    }
}
