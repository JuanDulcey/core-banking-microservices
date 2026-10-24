package com.devv.banking.cuentas.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cliente_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class ClienteSnapshotEntity {

    @Id
    @Column(name = "persona_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cliente_id", nullable = false, unique = true, length = 50)
    private String clienteId;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "identificacion", length = 30)
    private String identificacion;

    @Column(name = "estado", nullable = false)
    private boolean estado;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
