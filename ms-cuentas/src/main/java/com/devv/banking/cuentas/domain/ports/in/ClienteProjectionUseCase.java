package com.devv.banking.cuentas.domain.ports.in;

import java.time.Instant;
import java.util.UUID;

/**
 * Puerto de Entrada (Driving Port): procesa eventos asíncronos de integración provenientes de {@code ms-clientes}
 * para mantener actualizada la proyección local de lectura ({@code cliente_snapshot}).
 */
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
