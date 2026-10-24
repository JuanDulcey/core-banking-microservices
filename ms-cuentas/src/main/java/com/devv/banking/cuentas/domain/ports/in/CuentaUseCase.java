package com.devv.banking.cuentas.domain.ports.in;

import com.devv.banking.cuentas.domain.model.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaUseCase {

    CuentaDetalle crear(CrearCuentaCommand command);

    CuentaDetalle obtenerPorIdentificador(String identificador);

    List<CuentaDetalle> listar(String clienteFiltro, Boolean estado);

    CuentaDetalle actualizar(String identificador, ActualizarCuentaCommand command);

    CuentaDetalle actualizarParcial(String identificador, ActualizarCuentaCommand command);

    record CrearCuentaCommand(
            String numeroCuenta,
            String tipoCuenta,
            BigDecimal saldoInicial,
            Boolean estado,
            String clienteReferencia
    ) {}

    record ActualizarCuentaCommand(
            String tipoCuenta,
            Boolean estado
    ) {}

    record CuentaDetalle(
            Cuenta cuenta,
            String clienteCodigo,
            String clienteNombre
    ) {}
}
