package com.devv.banking.clientes.domain.ports.out;

/**
 * Puerto de Salida (Driven Port): desacopla al núcleo de aplicación del algoritmo concreto
 * de hashing criptográfico de contraseñas (ej. BCrypt).
 */
public interface PasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
