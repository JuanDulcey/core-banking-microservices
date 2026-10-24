package com.devv.banking.cuentas.domain.exception;

/**
 * Jerarquía raíz de excepciones de negocio del dominio contable ({@code ms-cuentas}).
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
