package com.devv.banking.cuentas.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Proyección local de Cliente sincronizada asíncronamente vía eventos RabbitMQ (Event-Carried State Transfer).
 */
public class ClienteSnapshot {

    private final UUID id;
    private String clienteId;
    private String nombre;
    private String identificacion;
    private boolean estado;
    private Instant updatedAt;

    public ClienteSnapshot(
            UUID id,
            String clienteId,
            String nombre,
            String identificacion,
            boolean estado,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "El id del cliente no puede ser nulo");
        this.clienteId = Objects.requireNonNull(clienteId, "El clienteId no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre del cliente no puede ser nulo");
        this.identificacion = identificacion;
        this.estado = estado;
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public void actualizarDesdeEvento(
            String clienteId,
            String nombre,
            String identificacion,
            boolean estado,
            Instant occurredAt
    ) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.estado = estado;
        this.updatedAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public void marcarInactivo(Instant occurredAt) {
        this.estado = false;
        this.updatedAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public boolean isEstado() {
        return estado;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
