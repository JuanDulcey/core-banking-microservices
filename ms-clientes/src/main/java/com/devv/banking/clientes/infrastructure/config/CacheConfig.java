package com.devv.banking.clientes.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché en memoria (Caffeine) para acelerar consultas frecuentes en ms-clientes.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
