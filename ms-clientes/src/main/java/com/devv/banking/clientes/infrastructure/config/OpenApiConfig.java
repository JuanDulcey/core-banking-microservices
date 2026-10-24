package com.devv.banking.clientes.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Core Banking — Microservicio de Clientes (ms-clientes)")
                        .version("1.0.0")
                        .description("API REST para gestión de Personas y Clientes con Arquitectura Hexagonal y eventos asíncronos RabbitMQ.")
                        .contact(new Contact()
                                .name("Juan Dulcey")
                                .url("https://github.com/JuanDulcey")));
    }
}
