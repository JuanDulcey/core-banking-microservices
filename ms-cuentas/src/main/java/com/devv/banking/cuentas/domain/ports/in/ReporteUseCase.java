package com.devv.banking.cuentas.domain.ports.in;

import com.devv.banking.cuentas.domain.model.EstadoCuentaReporte;

public interface ReporteUseCase {

    EstadoCuentaReporte generarEstadoCuenta(String rangoFechas, String fechaInicio, String fechaFin, String clienteReferencia);
}
