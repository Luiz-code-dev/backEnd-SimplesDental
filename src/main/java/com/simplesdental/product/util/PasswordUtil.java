package com.simplesdental.product.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "KMbT%5wT*R!46i@@YHqx";
        String hash = encoder.encode(password);
        System.out.println("Hash para a senha '" + password + "': " + hash);
        System.out.println("Verificação: " + encoder.matches(password, hash));
    }
}
