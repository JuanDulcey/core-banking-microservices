package com.devv.banking.cuentas.infrastructure.controllers;

import com.devv.banking.cuentas.application.dto.EstadoCuentaReporteResponse;
import com.devv.banking.cuentas.domain.model.EstadoCuentaReporte;
import com.devv.banking.cuentas.domain.ports.in.ReporteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador Primario REST (Driving Adapter) para generación de reportes de Estado de Cuenta (F4).
 * Soporta {@code /api/reportes} y {@code /reportes}, admitiendo el parámetro {@code cliente} o su alias {@code clienteId}.
 */
@RestController
@RequestMapping({"/api/reportes", "/reportes"})
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Generación de Estado de Cuenta por rango de fechas y cliente (F4)")
public class ReporteController {

    private final ReporteUseCase reporteUseCase;

    @GetMapping
    @Operation(summary = "Generar reporte de estado de cuenta en formato JSON")
    public ResponseEntity<?> generarReporte(
            @Parameter(description = "Rango de fechas separado por coma, ej: 2022-02-01,2022-02-28 o 8/2/2022,10/2/2022")
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @Parameter(description = "clienteId, UUID, identificación o nombre del cliente")
            @RequestParam(required = false) String cliente,
            @Parameter(description = "Alias opcional si se envía ?clienteId=... en lugar de ?cliente=...")
            @RequestParam(required = false) String clienteId,
            @Parameter(description = "Formato opcional: 'completo' (por defecto, cuentas + movimientos) o 'plano' (solo arreglo de movimientos)")
            @RequestParam(defaultValue = "completo") String formato
    ) {
        String clienteRef = (cliente != null && !cliente.isBlank()) ? cliente : clienteId;
        EstadoCuentaReporte reporte = reporteUseCase.generarEstadoCuenta(fecha, fechaInicio, fechaFin, clienteRef);
        EstadoCuentaReporteResponse response = EstadoCuentaReporteResponse.fromDomain(reporte);
        if ("plano".equalsIgnoreCase(formato)) {
            return ResponseEntity.ok(response.movimientos());
        }
        return ResponseEntity.ok(response);
    }
}
