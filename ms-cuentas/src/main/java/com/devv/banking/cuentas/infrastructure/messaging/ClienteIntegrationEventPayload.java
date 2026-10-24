package com.devv.banking.cuentas.infrastructure.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload JSON desacoplado recibido por RabbitMQ cuando {@code ms-clientes} emite un evento de integración.
 */
public record ClienteIntegrationEventPayload(
        UUID eventId,
        String eventType,
        UUID id,
        String clienteId,
        String nombre,
        String identificacion,
        boolean estado,
        Instant occurredAt
) {}
