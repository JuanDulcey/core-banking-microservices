package com.devv.banking.cuentas.domain.ports.in;

import com.devv.banking.cuentas.domain.model.EstadoCuentaReporte;

/**
 * Puerto de Entrada (Driving Port): generación de reportes de Estado de Cuenta por rango de fechas y cliente (F4).
 */
public interface ReporteUseCase {

    EstadoCuentaReporte generarEstadoCuenta(String rangoFechas, String fechaInicio, String fechaFin, String clienteReferencia);
}
