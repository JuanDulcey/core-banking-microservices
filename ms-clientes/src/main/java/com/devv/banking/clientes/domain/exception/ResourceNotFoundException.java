package com.devv.banking.clientes.domain.exception;

/**
 * Excepción de dominio lanzada cuando no existe un cliente con el identificador solicitado.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
