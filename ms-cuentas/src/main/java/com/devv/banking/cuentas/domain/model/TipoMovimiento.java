package com.devv.banking.cuentas.domain.model;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Clasificación contable de un movimiento bancario (ingreso por depósito o egreso por retiro).
 */
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
            String sinTildes = Normalizer.normalize(textoOpcional.trim(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "");
            String upper = sinTildes.toUpperCase(Locale.ROOT);
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
