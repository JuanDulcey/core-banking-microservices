package com.devv.banking.cuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para actualización parcial de una cuenta (PATCH)")
public record CuentaPatchRequest(

        @JsonAlias({"tipo", "tipo_cuenta", "Tipo"})
        String tipoCuenta,

        @JsonAlias({"Estado"})
        Boolean estado
) {}
