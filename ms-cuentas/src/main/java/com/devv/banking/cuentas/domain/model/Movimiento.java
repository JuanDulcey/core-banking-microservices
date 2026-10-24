package com.devv.banking.cuentas.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio Movimiento que representa un asiento transaccional contable sobre una Cuenta.
 */
public class Movimiento {

    private final UUID id;
    private final UUID cuentaId;
    private final String numeroCuenta;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal saldoInicial;
    private BigDecimal valor;
    private BigDecimal saldo;

    public Movimiento(
            UUID id,
            UUID cuentaId,
            String numeroCuenta,
            LocalDateTime fecha,
            TipoMovimiento tipoMovimiento,
            BigDecimal saldoInicial,
            BigDecimal valor,
            BigDecimal saldo
    ) {
        this.id = Objects.requireNonNull(id, "El id del movimiento no puede ser nulo");
        this.cuentaId = Objects.requireNonNull(cuentaId, "El cuentaId no puede ser nulo");
        this.numeroCuenta = Objects.requireNonNull(numeroCuenta, "El número de cuenta no puede ser nulo");
        this.fecha = fecha != null ? fecha : LocalDateTime.now();
        this.tipoMovimiento = Objects.requireNonNull(tipoMovimiento, "El tipo de movimiento no puede ser nulo");
        this.saldoInicial = escalarMoneda(Objects.requireNonNull(saldoInicial, "El saldo inicial previo no puede ser nulo"));
        this.valor = escalarMoneda(Objects.requireNonNull(valor, "El valor del movimiento no puede ser nulo"));
        this.saldo = escalarMoneda(Objects.requireNonNull(saldo, "El saldo disponible resultante no puede ser nulo"));
    }

    public void actualizarFecha(LocalDateTime nuevaFecha) {
        if (nuevaFecha != null) {
            this.fecha = nuevaFecha;
        }
    }

    public void actualizarValoresContables(TipoMovimiento nuevoTipo, BigDecimal nuevoValor, BigDecimal nuevoSaldoResultante) {
        this.tipoMovimiento = Objects.requireNonNull(nuevoTipo);
        this.valor = escalarMoneda(nuevoValor);
        this.saldo = escalarMoneda(nuevoSaldoResultante);
    }

    private static BigDecimal escalarMoneda(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCuentaId() {
        return cuentaId;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
