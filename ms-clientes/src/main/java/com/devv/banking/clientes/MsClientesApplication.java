package com.devv.banking.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del Microservicio de Identidad y Clientes ({@code ms-clientes}).
 * <p>
 * Gestiona el contexto acotado (Bounded Context) de {@code Persona} y {@code Cliente}
 * bajo Arquitectura Hexagonal, publicando eventos de integración asíncronos hacia RabbitMQ.
 */
@SpringBootApplication
public class MsClientesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsClientesApplication.class, args);
    }
}
