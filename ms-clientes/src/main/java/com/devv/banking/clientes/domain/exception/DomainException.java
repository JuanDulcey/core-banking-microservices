package com.devv.banking.clientes.domain.exception;

/**
 * Jerarquía raíz de excepciones de negocio del dominio de clientes.
 * Permite desacoplar los errores semánticos del dominio de códigos de transporte HTTP.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
