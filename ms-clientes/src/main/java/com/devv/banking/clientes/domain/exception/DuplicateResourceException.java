package com.devv.banking.clientes.domain.exception;

/**
 * Excepción de dominio lanzada ante intentos de registrar claves únicas duplicadas
 * ({@code clienteId} o {@code identificacion}).
 */
public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
