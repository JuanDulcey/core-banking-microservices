package com.devv.banking.cuentas.domain.ports.in;

import com.devv.banking.cuentas.domain.model.Movimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MovimientoUseCase {

    Movimiento registrar(RegistrarMovimientoCommand command);

    Movimiento obtenerPorId(UUID id);

    List<Movimiento> listar(String numeroCuenta);

    Movimiento actualizar(UUID id, ActualizarMovimientoCommand command);

    record RegistrarMovimientoCommand(
            String numeroCuenta,
            String tipoMovimiento,
            BigDecimal valor,
            LocalDateTime fecha
    ) {}

    record ActualizarMovimientoCommand(
            String tipoMovimiento,
            BigDecimal valor,
            LocalDateTime fecha
    ) {}
}
