package com.devv.banking.cuentas.application.dto;

import com.devv.banking.cuentas.domain.ports.in.CuentaUseCase.CuentaDetalle;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO de salida con el detalle público de una cuenta bancaria y los datos proyectados de su titular.
 */
@Schema(description = "Representación de una cuenta bancaria")
public record CuentaResponse(
        UUID id,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        BigDecimal saldoDisponible,
        boolean estado,
        UUID clienteUuid,
        String clienteId,
        String cliente,
        Instant createdAt,
        Instant updatedAt
) {
    public static CuentaResponse fromDetalle(CuentaDetalle detalle) {
        return new CuentaResponse(
                detalle.cuenta().getId(),
                detalle.cuenta().getNumeroCuenta(),
                detalle.cuenta().getTipoCuenta().getEtiqueta(),
                detalle.cuenta().getSaldoInicial(),
                detalle.cuenta().getSaldoDisponible(),
                detalle.cuenta().isEstado(),
                detalle.cuenta().getClienteId(),
                detalle.clienteCodigo(),
                detalle.clienteNombre(),
                detalle.cuenta().getCreatedAt(),
                detalle.cuenta().getUpdatedAt()
        );
    }
}
