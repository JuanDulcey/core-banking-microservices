package com.devv.banking.cuentas.domain.model;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;
import com.devv.banking.cuentas.domain.exception.SaldoNoDisponibleException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz Cuenta. Encapsula las reglas financieras de saldo disponible, depósitos y retiros.
 */
public class Cuenta {

    private final UUID id;
    private final String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private final BigDecimal saldoInicial;
    private BigDecimal saldoDisponible;
    private boolean estado;
    private final UUID clienteId;
    private final Long version;
    private final Instant createdAt;
    private Instant updatedAt;

    public Cuenta(
            UUID id,
            String numeroCuenta,
            TipoCuenta tipoCuenta,
            BigDecimal saldoInicial,
            BigDecimal saldoDisponible,
            boolean estado,
            UUID clienteId,
            Long version,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "El id de la cuenta no puede ser nulo");
        this.numeroCuenta = validarNumeroCuenta(numeroCuenta);
        this.tipoCuenta = Objects.requireNonNull(tipoCuenta, "El tipo de cuenta es obligatorio");
        this.saldoInicial = validarMontoNoNegativo(saldoInicial, "El saldo inicial no puede ser negativo");
        this.saldoDisponible = validarMontoNoNegativo(
                saldoDisponible != null ? saldoDisponible : this.saldoInicial,
                "El saldo disponible no puede ser negativo"
        );
        this.estado = estado;
        this.clienteId = Objects.requireNonNull(clienteId, "El clienteId asociado es obligatorio");
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public static Cuenta abrirNueva(
            String numeroCuenta,
            TipoCuenta tipoCuenta,
            BigDecimal saldoInicial,
            Boolean estado,
            UUID clienteId
    ) {
        BigDecimal apertura = validarMontoNoNegativo(saldoInicial, "El saldo inicial no puede ser negativo");
        Instant ahora = Instant.now();
        return new Cuenta(
                UUID.randomUUID(),
                numeroCuenta,
                tipoCuenta,
                apertura,
                apertura,
                estado == null || estado,
                clienteId,
                null,
                ahora,
                ahora
        );
    }

    /**
     * Registra un movimiento contable (F2 y F3), actualizando atómicamente el saldo disponible
     * o rechazando con {@link SaldoNoDisponibleException} si el saldo resultante es menor a cero.
     */
    public Movimiento registrarMovimiento(BigDecimal valorSolicitado, String tipoMovimientoTexto, LocalDateTime fechaMovimiento) {
        if (!this.estado) {
            throw new BusinessValidationException("No se pueden registrar movimientos en una cuenta inactiva: " + this.numeroCuenta);
        }
        BigDecimal valorEfectivo = normalizarValorMovimiento(valorSolicitado, tipoMovimientoTexto);
        TipoMovimiento tipoResuelto = TipoMovimiento.resolver(tipoMovimientoTexto, valorEfectivo);

        BigDecimal saldoPrevio = this.saldoDisponible;
        BigDecimal nuevoSaldo = saldoPrevio.add(valorEfectivo).setScale(2, RoundingMode.HALF_UP);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException();
        }

        this.saldoDisponible = nuevoSaldo;
        this.updatedAt = Instant.now();

        return new Movimiento(
                UUID.randomUUID(),
                this.id,
                this.numeroCuenta,
                fechaMovimiento != null ? fechaMovimiento : LocalDateTime.now(),
                tipoResuelto,
                saldoPrevio,
                valorEfectivo,
                nuevoSaldo
        );
    }

    /**
     * Recalcula un movimiento existente (CRU en Movimiento) validando que el nuevo saldo no quede en negativo.
     */
    public void recalcularUltimoMovimiento(Movimiento movimiento, BigDecimal nuevoValorSolicitado, String nuevoTipoTexto, LocalDateTime nuevaFecha) {
        if (!this.estado) {
            throw new BusinessValidationException("No se pueden modificar movimientos de una cuenta inactiva: " + this.numeroCuenta);
        }
        BigDecimal nuevoValorEfectivo = normalizarValorMovimiento(nuevoValorSolicitado, nuevoTipoTexto);
        TipoMovimiento nuevoTipo = TipoMovimiento.resolver(nuevoTipoTexto, nuevoValorEfectivo);

        BigDecimal saldoBaseAntesDelMovimiento = movimiento.getSaldoInicial();
        BigDecimal nuevoSaldoResultante = saldoBaseAntesDelMovimiento.add(nuevoValorEfectivo).setScale(2, RoundingMode.HALF_UP);

        if (nuevoSaldoResultante.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException();
        }

        movimiento.actualizarValoresContables(nuevoTipo, nuevoValorEfectivo, nuevoSaldoResultante);
        movimiento.actualizarFecha(nuevaFecha);
        this.saldoDisponible = nuevoSaldoResultante;
        this.updatedAt = Instant.now();
    }

    public void actualizarDatosCuenta(TipoCuenta nuevoTipo, Boolean nuevoEstado) {
        if (nuevoTipo != null) {
            this.tipoCuenta = nuevoTipo;
        }
        if (nuevoEstado != null) {
            this.estado = nuevoEstado;
        }
        this.updatedAt = Instant.now();
    }

    private static BigDecimal normalizarValorMovimiento(BigDecimal valorSolicitado, String tipoMovimientoTexto) {
        if (valorSolicitado == null || valorSolicitado.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessValidationException("El valor del movimiento es obligatorio y debe ser diferente de cero");
        }
        BigDecimal escalado = valorSolicitado.setScale(2, RoundingMode.HALF_UP);

        if (tipoMovimientoTexto != null && !tipoMovimientoTexto.isBlank()) {
            TipoMovimiento tipoExplicito = TipoMovimiento.resolver(tipoMovimientoTexto, escalado);
            if (tipoExplicito == TipoMovimiento.RETIRO && escalado.signum() > 0) {
                return escalado.negate();
            }
            if (tipoExplicito == TipoMovimiento.DEPOSITO && escalado.signum() < 0) {
                throw new BusinessValidationException("Un movimiento de tipo Depósito no puede tener valor negativo");
            }
        }
        return escalado;
    }

    private static String validarNumeroCuenta(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new BusinessValidationException("El número de cuenta es obligatorio");
        }
        return numero.trim();
    }

    private static BigDecimal validarMontoNoNegativo(BigDecimal monto, String mensajeError) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException(mensajeError);
        }
        return monto.setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getId() {
        return id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public boolean isEstado() {
        return estado;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
