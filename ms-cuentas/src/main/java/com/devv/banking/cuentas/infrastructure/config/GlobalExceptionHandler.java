package com.devv.banking.cuentas.infrastructure.config;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;
import com.devv.banking.cuentas.domain.exception.DuplicateResourceException;
import com.devv.banking.cuentas.domain.exception.ResourceNotFoundException;
import com.devv.banking.cuentas.domain.exception.SaldoNoDisponibleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaldoNoDisponibleException.class)
    public ProblemDetail handleSaldoNoDisponible(SaldoNoDisponibleException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Fondos insuficientes", ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.CONFLICT, "Conflicto de unicidad", ex.getMessage(), request);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleConcurrencyConflict(OptimisticLockingFailureException ex, HttpServletRequest request) {
        return buildProblem(
                HttpStatus.CONFLICT,
                "Conflicto de concurrencia",
                "La cuenta fue modificada simultáneamente por otra operación; por favor reintente",
                request
        );
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ProblemDetail handleBusinessValidation(BusinessValidationException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Regla de negocio inválida", ex.getMessage(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Parámetro requerido faltante",
                "Falta el parámetro obligatorio: " + ex.getParameterName(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        ProblemDetail problem = buildProblem(
                HttpStatus.BAD_REQUEST,
                "Error de validación de entrada",
                "Uno o más campos de la petición no son válidos",
                request
        );
        problem.setProperty("errors", fieldErrors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Cuerpo JSON inválido", "El formato del JSON enviado no es válido", request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado procesando {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", "Ocurrió un error inesperado procesando la solicitud", request);
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("message", detail);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
