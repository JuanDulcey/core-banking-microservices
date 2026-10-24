package com.devv.banking.cuentas.domain.model;

import java.math.BigDecimal;
import java.util.Locale;

public enum TipoMovimiento {
    DEPOSITO("Deposito"),
    RETIRO("Retiro");

    private final String etiqueta;

    TipoMovimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static TipoMovimiento resolver(String textoOpcional, BigDecimal valor) {
        if (textoOpcional != null && !textoOpcional.isBlank()) {
            String upper = textoOpcional.trim().toUpperCase(Locale.ROOT);
            if (upper.startsWith("RET") || upper.startsWith("DEB") || upper.startsWith("EGR")) {
                return RETIRO;
            }
            if (upper.startsWith("DEP") || upper.startsWith("CRE") || upper.startsWith("ING") || upper.startsWith("ABO")) {
                return DEPOSITO;
            }
        }
        return valor.signum() < 0 ? RETIRO : DEPOSITO;
    }
}
