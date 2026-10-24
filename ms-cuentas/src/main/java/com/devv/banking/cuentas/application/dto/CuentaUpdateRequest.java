package com.devv.banking.cuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para actualización completa de una cuenta (PUT)")
public record CuentaUpdateRequest(

        @NotBlank(message = "El tipo de cuenta es obligatorio")
        @JsonAlias({"tipo", "tipo_cuenta", "Tipo"})
        @Schema(example = "Corriente")
        String tipoCuenta,

        @NotNull(message = "El estado de la cuenta es obligatorio")
        @JsonAlias({"Estado"})
        @Schema(example = "true")
        Boolean estado
) {}
