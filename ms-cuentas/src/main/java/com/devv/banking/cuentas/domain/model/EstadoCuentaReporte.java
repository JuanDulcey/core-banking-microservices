package com.devv.banking.cuentas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Modelo de dominio inmutable para el Reporte de Estado de Cuenta (F4).
 */
public record EstadoCuentaReporte(
        ClienteSnapshot cliente,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal totalCreditos,
        BigDecimal totalDebitos,
        List<ResumenCuenta> cuentas,
        List<DetalleMovimientoReporte> movimientos
) {
    public record ResumenCuenta(
            String numeroCuenta,
            String tipoCuenta,
            BigDecimal saldoInicialApertura,
            BigDecimal saldoDisponibleActual,
            boolean estado,
            int cantidadMovimientosEnRango
    ) {}

    public record DetalleMovimientoReporte(
            String fecha,
            String cliente,
            String numeroCuenta,
            String tipo,
            BigDecimal saldoInicial,
            boolean estado,
            BigDecimal movimiento,
            BigDecimal saldoDisponible
    ) {}
}
