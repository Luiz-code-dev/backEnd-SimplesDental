package com.simplesdental.product.service;

import com.simplesdental.product.dto.RegisterRequest;
import com.simplesdental.product.dto.UserResponse;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        logger.debug("Getting all users with pagination: {}", pageable);
        return userRepository.findAll(pageable).map(UserResponse::fromUser);
    }

    @Transactional
    public UserResponse createUser(RegisterRequest request) {
        logger.debug("Creating new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("User creation failed - email already exists: {}", request.getEmail());
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
        logger.info("User created successfully: {}", request.getEmail());
        
        return UserResponse.fromUser(savedUser);
    }
}
