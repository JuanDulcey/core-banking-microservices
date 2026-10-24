package com.devv.banking.cuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Solicitud para registrar un movimiento contable en una cuenta")
public record MovimientoCreateRequest(

        @NotBlank(message = "El número de cuenta es obligatorio")
        @JsonAlias({"numero_cuenta", "Numero Cuenta"})
        @Schema(example = "478758")
        String numeroCuenta,

        @JsonAlias({"tipo", "tipo_movimiento", "Tipo Movimiento"})
        @Schema(example = "Retiro", description = "Opcional; si no se envía, se infiere del signo del valor (+ Deposito / - Retiro)")
        String tipoMovimiento,

        @NotNull(message = "El valor del movimiento es obligatorio")
        @JsonAlias({"movimiento", "Movimiento", "monto"})
        @Schema(example = "-575.00")
        BigDecimal valor,

        @JsonAlias({"Fecha"})
        @Schema(example = "2022-02-10", description = "Opcional; acepta yyyy-MM-dd, d/M/yyyy o yyyy-MM-ddTHH:mm:ss")
        String fecha
) {}
