package com.devv.banking.cuentas.domain.exception;

/**
 * Excepción de dominio lanzada al intentar registrar un {@code numeroCuenta} ya existente.
 */
public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
