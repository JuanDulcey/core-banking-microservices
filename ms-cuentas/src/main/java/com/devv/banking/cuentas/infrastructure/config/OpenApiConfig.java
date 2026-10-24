package com.devv.banking.cuentas.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de especificación OpenAPI 3 / Swagger UI para documentar los endpoints de ms-cuentas.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Core Banking — Microservicio de Cuentas y Movimientos (ms-cuentas)")
                        .version("1.0.0")
                        .description("API REST para gestión de Cuentas, Movimientos transaccionales y Reportes de Estado de Cuenta.")
                        .contact(new Contact()
                                .name("Juan Dulcey")
                                .url("https://github.com/JuanDulcey")));
    }
}
