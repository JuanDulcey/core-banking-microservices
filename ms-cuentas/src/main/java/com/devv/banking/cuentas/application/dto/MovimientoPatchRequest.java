package com.devv.banking.cuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO de entrada para rectificación parcial (PATCH) del último movimiento de una cuenta.
 */
@Schema(description = "Solicitud para actualización parcial de un movimiento (PATCH)")
public record MovimientoPatchRequest(

        @JsonAlias({"tipo", "tipo_movimiento", "Tipo Movimiento"})
        String tipoMovimiento,

        @JsonAlias({"movimiento", "Movimiento", "monto"})
        BigDecimal valor,

        @JsonAlias({"Fecha"})
        String fecha
) {}
