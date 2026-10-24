package com.devv.banking.cuentas.domain.model;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;

import java.util.Locale;

public enum TipoCuenta {
    AHORROS("Ahorros"),
    CORRIENTE("Corriente");

    private final String etiqueta;

    TipoCuenta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static TipoCuenta fromText(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessValidationException("El tipo de cuenta es obligatorio (Ahorros o Corriente)");
        }
        String normalizado = valor.trim().toUpperCase(Locale.ROOT);
        return switch (normalizado) {
            case "AHORROS", "AHORRO", "SAVINGS" -> AHORROS;
            case "CORRIENTE", "CHECKING" -> CORRIENTE;
            default -> throw new BusinessValidationException(
                    "Tipo de cuenta no válido: '" + valor + "'. Valores permitidos: Ahorros, Corriente"
            );
        };
    }
}
