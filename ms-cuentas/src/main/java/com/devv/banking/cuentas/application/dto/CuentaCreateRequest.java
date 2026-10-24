package com.devv.banking.cuentas.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Solicitud para apertura de una cuenta bancaria")
public record CuentaCreateRequest(

        @NotBlank(message = "El número de cuenta es obligatorio")
        @Size(max = 30, message = "El número de cuenta no debe exceder 30 caracteres")
        @JsonAlias({"numero_cuenta", "Numero Cuenta"})
        @Schema(example = "478758")
        String numeroCuenta,

        @NotBlank(message = "El tipo de cuenta es obligatorio")
        @JsonAlias({"tipo", "tipo_cuenta", "Tipo"})
        @Schema(example = "Ahorros")
        String tipoCuenta,

        @NotNull(message = "El saldo inicial es obligatorio")
        @DecimalMin(value = "0.00", message = "El saldo inicial no puede ser negativo")
        @JsonAlias({"saldo_inicial", "Saldo Inicial"})
        @Schema(example = "2000.00")
        BigDecimal saldoInicial,

        @JsonAlias({"Estado"})
        @Schema(example = "true", defaultValue = "true")
        Boolean estado,

        @NotBlank(message = "La referencia del cliente (clienteId, UUID, identificación o nombre) es obligatoria")
        @JsonAlias({"cliente", "Cliente", "cliente_id", "clienteNombre"})
        @Schema(example = "Jose Lema")
        String clienteId
) {}
