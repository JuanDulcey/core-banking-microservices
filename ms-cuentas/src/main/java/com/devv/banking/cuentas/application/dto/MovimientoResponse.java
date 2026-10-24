package com.devv.banking.cuentas.application.dto;

import com.devv.banking.cuentas.domain.model.Movimiento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida que expone el asiento contable de un {@link Movimiento} con saldo previo y resultante.
 */
@Schema(description = "Representación de un movimiento bancario registrado")
public record MovimientoResponse(
        UUID id,
        String numeroCuenta,
        LocalDateTime fecha,
        String tipoMovimiento,
        BigDecimal saldoInicial,
        BigDecimal valor,
        BigDecimal saldo
) {
    public static MovimientoResponse fromDomain(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getNumeroCuenta(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento().getEtiqueta(),
                movimiento.getSaldoInicial(),
                movimiento.getValor(),
                movimiento.getSaldo()
        );
    }
}
