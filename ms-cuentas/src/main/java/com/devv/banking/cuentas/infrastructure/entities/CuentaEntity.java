package com.devv.banking.cuentas.infrastructure.entities;

import com.devv.banking.cuentas.domain.model.TipoCuenta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA para la tabla {@code cuentas}.
 * Incluye control de versión ({@link Version}) para bloqueo optimista complementario al bloqueo pesimista.
 */
@Entity
@Table(name = "cuentas")
@Getter
@Setter
@NoArgsConstructor
public class CuentaEntity {

    @Id
    @Column(name = "cuenta_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 30)
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    private TipoCuenta tipoCuenta;

    @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "saldo_disponible", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoDisponible;

    @Column(name = "estado", nullable = false)
    private boolean estado;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
