package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthRequest;
import com.simplesdental.product.dto.AuthResponse;
import com.simplesdental.product.dto.RegisterRequest;
import com.simplesdental.product.dto.UserContextResponse;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import com.simplesdental.product.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            logger.debug("Attempting authentication for user: {}", request.getEmail());

            // Autentica o usuário usando o AuthenticationManager
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            // Se a autenticação foi bem sucedida, busca o usuário e gera o token
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        logger.warn("User not found: {}", request.getEmail());
                        return new BadCredentialsException("Invalid email or password");
                    });

            var jwtToken = jwtService.generateToken(user);
            logger.debug("Generated JWT token for user {}: {}", request.getEmail(), jwtToken);
            logger.info("Authentication successful for user: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {}", request.getEmail());
            throw e;
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.debug("Processing registration for user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        logger.info("User registered successfully: {}", request.getEmail());
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public UserContextResponse getUserContext(String email) {
        logger.debug("Getting user context for email: {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found while getting context: {}", email);
                    return new IllegalStateException("User not found");
                });

        logger.debug("User context retrieved successfully for: {}", email);
        return UserContextResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
