package com.devv.banking.cuentas.domain.exception;

/**
 * Excepción de dominio lanzada cuando una cuenta, movimiento o cliente proyectado no existe.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
