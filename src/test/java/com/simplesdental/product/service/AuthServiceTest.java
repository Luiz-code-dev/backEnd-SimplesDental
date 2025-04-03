package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthRequest;
import com.simplesdental.product.dto.RegisterRequest;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import com.simplesdental.product.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        var email = "contato@simplesdental.com";
        var password = "KMbT%5wT*R!46i@@YHqx";
        var request = AuthRequest.builder()
                .email(email)
                .password(password)
                .build();

        var user = User.builder()
                .email(email)
                .password("$2a$10$3vQkOBwmXFZqHgGt0xUJCuGFKhK9HhZ0B3GYYTxUjgWmLxR6PiTZO")
                .firstName("Admin")
                .lastName("User")
                .role(User.Role.ADMIN)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );

        // Act
        var response = authService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        var email = "contato@simplesdental.com";
        var password = "wrong_password";
        var request = AuthRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void register_WithNewUser_ShouldCreateUserAndReturnToken() {
        // Arrange
        var request = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .role(User.Role.USER)
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        // Act
        var response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Arrange
        var request = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .role(User.Role.USER)
                .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserContext_WithValidEmail_ShouldReturnUserContext() {
        // Arrange
        var email = "test@example.com";
        var user = User.builder()
                .id(1L)
                .email(email)
                .firstName("Test")
                .lastName("User")
                .role(User.Role.USER)
                .build();

        when(jwtService.extractUsername(anyString())).thenReturn(email);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        var response = authService.getUserContext("token");

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(email, response.getEmail());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals(User.Role.USER, response.getRole());
    }

    @Test
    void getUserContext_WithInvalidEmail_ShouldThrowException() {
        // Arrange
        var email = "test@example.com";

        when(jwtService.extractUsername(anyString())).thenReturn(email);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> authService.getUserContext("token"));
    }
}
