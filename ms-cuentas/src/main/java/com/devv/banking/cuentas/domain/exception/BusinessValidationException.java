package com.devv.banking.cuentas.domain.exception;

/**
 * Excepción de dominio lanzada cuando una operación incumple reglas o invariantes contables.
 */
public class BusinessValidationException extends DomainException {

    public BusinessValidationException(String message) {
        super(message);
    }
}
