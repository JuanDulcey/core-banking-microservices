package com.devv.banking.cuentas.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché en memoria (Caffeine) para optimizar consultas en ms-cuentas.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
