package com.devv.banking.cuentas.application.dto;

import com.devv.banking.cuentas.domain.model.EstadoCuentaReporte;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO compuesto del reporte de Estado de Cuenta (F4), incluyendo el resumen de cuentas asociadas
 * y el listado detallado de movimientos en formato JSON.
 */
@Schema(description = "Estado de cuenta consolidado por cliente y rango de fechas (F4)")
public record EstadoCuentaReporteResponse(
        String clienteId,
        String cliente,
        String fechaInicio,
        String fechaFin,
        BigDecimal totalCreditos,
        BigDecimal totalDebitos,
        List<CuentaResumenResponse> cuentas,
        List<MovimientoEstadoCuentaItemResponse> movimientos
) {
    public record CuentaResumenResponse(
            String numeroCuenta,
            String tipoCuenta,
            BigDecimal saldoInicial,
            BigDecimal saldoDisponible,
            boolean estado,
            int cantidadMovimientos
    ) {}

    /**
     * Respeta exactamente la estructura y nombres de propiedades del ejemplo JSON del Caso de Uso 5.
     */
    public record MovimientoEstadoCuentaItemResponse(
            @JsonProperty("Fecha") String fecha,
            @JsonProperty("Cliente") String cliente,
            @JsonProperty("Numero Cuenta") String numeroCuenta,
            @JsonProperty("Tipo") String tipo,
            @JsonProperty("Saldo Inicial") BigDecimal saldoInicial,
            @JsonProperty("Estado") boolean estado,
            @JsonProperty("Movimiento") BigDecimal movimiento,
            @JsonProperty("Saldo Disponible") BigDecimal saldoDisponible
    ) {}

    public static EstadoCuentaReporteResponse fromDomain(EstadoCuentaReporte reporte) {
        List<CuentaResumenResponse> cuentasDto = reporte.cuentas().stream()
                .map(c -> new CuentaResumenResponse(
                        c.numeroCuenta(),
                        c.tipoCuenta(),
                        c.saldoInicialApertura(),
                        c.saldoDisponibleActual(),
                        c.estado(),
                        c.cantidadMovimientosEnRango()
                ))
                .toList();

        List<MovimientoEstadoCuentaItemResponse> movimientosDto = reporte.movimientos().stream()
                .map(m -> new MovimientoEstadoCuentaItemResponse(
                        m.fecha(),
                        m.cliente(),
                        m.numeroCuenta(),
                        m.tipo(),
                        m.saldoInicial(),
                        m.estado(),
                        m.movimiento(),
                        m.saldoDisponible()
                ))
                .toList();

        return new EstadoCuentaReporteResponse(
                reporte.cliente().getClienteId(),
                reporte.cliente().getNombre(),
                reporte.fechaInicio().toString(),
                reporte.fechaFin().toString(),
                reporte.totalCreditos(),
                reporte.totalDebitos(),
                cuentasDto,
                movimientosDto
        );
    }
}
