package com.devv.banking.cuentas.infrastructure.messaging;

import java.time.Instant;
import java.util.UUID;

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
