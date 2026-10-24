package com.devv.banking.cuentas.domain.ports.in;

import java.time.Instant;
import java.util.UUID;

public interface ClienteProjectionUseCase {

    void sincronizarEventoCliente(SincronizarClienteCommand command);

    record SincronizarClienteCommand(
            String eventType,
            UUID id,
            String clienteId,
            String nombre,
            String identificacion,
            boolean estado,
            Instant occurredAt
    ) {}
}
