package com.devv.banking.cuentas.domain.exception;

/**
 * Excepción de dominio requerida en F3 cuando una cuenta no dispone de fondos suficientes.
 */
public class SaldoNoDisponibleException extends DomainException {

    public static final String MENSAJE_OFICIAL = "Saldo no disponible";

    public SaldoNoDisponibleException() {
        super(MENSAJE_OFICIAL);
    }
}
