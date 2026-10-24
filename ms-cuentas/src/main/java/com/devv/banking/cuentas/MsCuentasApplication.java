package com.devv.banking.cuentas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del Microservicio Contable de Cuentas, Movimientos y Reportes ({@code ms-cuentas}).
 * <p>
 * Mantiene una proyección asíncrona local de clientes vía RabbitMQ (Event-Carried State Transfer)
 * y garantiza integridad transaccional financiera sobre saldos disponibles.
 */
@SpringBootApplication
public class MsCuentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCuentasApplication.class, args);
    }
}
