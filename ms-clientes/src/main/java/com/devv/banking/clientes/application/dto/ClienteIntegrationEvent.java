package com.devv.banking.clientes.application.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Contrato de evento de integración publicado asíncronamente en RabbitMQ hacia otros microservicios.
 */
public record ClienteIntegrationEvent(
        UUID eventId,
        String eventType,
        UUID id,
        String clienteId,
        String nombre,
        String identificacion,
        boolean estado,
        Instant occurredAt
) {}
