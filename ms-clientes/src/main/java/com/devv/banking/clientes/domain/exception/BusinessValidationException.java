package com.devv.banking.clientes.domain.exception;

/**
 * Excepción lanzada cuando se viola una invariante o regla de validación de negocio en el dominio.
 */
public class BusinessValidationException extends DomainException {

    public BusinessValidationException(String message) {
        super(message);
    }
}
