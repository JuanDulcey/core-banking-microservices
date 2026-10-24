package com.devv.banking.clientes.application.dto;

import com.devv.banking.clientes.domain.model.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representación pública de un cliente (sin exponer contraseña)")
public record ClienteResponse(
        UUID id,
        String clienteId,
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        boolean estado,
        Instant createdAt,
        Instant updatedAt
) {
    public static ClienteResponse fromDomain(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.isEstado(),
                cliente.getCreatedAt(),
                cliente.getUpdatedAt()
        );
    }
}
