package com.simplesdental.product.config;

import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String ADMIN_EMAIL = "contato@simplesdental.com";
    private static final String ADMIN_PASSWORD = "KMbT%5wT*R!46i@@YHqx";

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // Verifica se o usuário admin já existe
                if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
                    logger.info("Criando usuário admin inicial...");
                    // Cria o usuário admin com a senha do README
                    User admin = User.builder()
                            .firstName("Admin")
                            .lastName("User")
                            .email(ADMIN_EMAIL)
                            .password(passwordEncoder.encode(ADMIN_PASSWORD))
                            .role(User.Role.ADMIN)
                            .build();
                    userRepository.save(admin);
                    logger.info("Usuário admin criado com sucesso!");
                } else {
                    logger.info("Usuário admin já existe.");
                }
            } catch (Exception e) {
                logger.error("Erro ao inicializar dados: {}", e.getMessage());
                throw e;
            }
        };
    }
}
