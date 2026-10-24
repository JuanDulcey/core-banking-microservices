package com.devv.banking.cuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO de entrada para rectificar de forma completa el último movimiento vigente ({@code PUT /api/movimientos/{id}}).
 */
@Schema(description = "Solicitud para actualizar un movimiento existente (PUT)")
public record MovimientoUpdateRequest(

        @JsonAlias({"tipo", "tipo_movimiento", "Tipo Movimiento"})
        @Schema(example = "Deposito")
        String tipoMovimiento,

        @NotNull(message = "El valor del movimiento es obligatorio")
        @JsonAlias({"movimiento", "Movimiento", "monto"})
        @Schema(example = "600.00")
        BigDecimal valor,

        @JsonAlias({"Fecha"})
        @Schema(example = "2022-02-10")
        String fecha
) {}
