package com.devv.banking.cuentas.infrastructure.config;

import com.devv.banking.cuentas.domain.exception.BusinessValidationException;
import com.devv.banking.cuentas.domain.exception.DuplicateResourceException;
import com.devv.banking.cuentas.domain.exception.ResourceNotFoundException;
import com.devv.banking.cuentas.domain.exception.SaldoNoDisponibleException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de errores HTTP en {@code ms-cuentas} bajo el estándar RFC 9457 ({@link ProblemDetail}).
 * Captura {@link SaldoNoDisponibleException} (F3) retornando HTTP 422 con el mensaje exacto {@code "Saldo no disponible"}.
 */
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Formato de parámetro inválido",
                "El valor proporcionado para '" + ex.getName() + "' no tiene un formato válido",
                request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildProblem(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP no permitido",
                "El método '" + ex.getMethod() + "' no está soportado para este recurso (operaciones permitidas: CRU)",
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.NOT_FOUND, "Ruta no encontrada", "No existe el endpoint solicitado: " + request.getRequestURI(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildProblem(HttpStatus.CONFLICT, "Conflicto de integridad de datos", "La operación infringe una restricción de unicidad o integridad referencial", request);
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
