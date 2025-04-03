package com.simplesdental.product.service;

import com.simplesdental.product.dto.RegisterRequest;
import com.simplesdental.product.dto.UpdateUserRequest;
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

    @Transactional
    public void deleteUser(Long id) {
        logger.debug("Deleting user with id: {}", id);
        
        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User deletion failed - user not found: {}", id);
                    return new IllegalArgumentException("User not found");
                });

        // Impede que o último usuário ADMIN seja deletado
        if (user.getRole() == User.Role.ADMIN) {
            long adminCount = userRepository.countByRole(User.Role.ADMIN);
            if (adminCount <= 1) {
                logger.warn("User deletion failed - cannot delete last admin user: {}", id);
                throw new IllegalStateException("Cannot delete the last admin user");
            }
        }

        userRepository.deleteById(id);
        logger.info("User deleted successfully: {}", id);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        logger.debug("Updating user with id: {}", id);
        
        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User update failed - user not found: {}", id);
                    return new IllegalArgumentException("User not found");
                });

        // Verifica se o email já existe para outro usuário
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            logger.warn("User update failed - email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        // Impede que o último admin seja alterado para USER
        if (user.getRole() == User.Role.ADMIN && 
            request.getRole() == User.Role.USER) {
            long adminCount = userRepository.countByRole(User.Role.ADMIN);
            if (adminCount <= 1) {
                logger.warn("User update failed - cannot change role of last admin user: {}", id);
                throw new IllegalStateException("Cannot change role of the last admin user");
            }
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        var savedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", id);
        
        return UserResponse.fromUser(savedUser);
    }
}
