package com.devv.banking.clientes.domain.model;

import com.devv.banking.clientes.domain.exception.BusinessValidationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad base de dominio Persona (100% Java puro, sin acoplamiento a frameworks).
 */
public class Persona {

    private final UUID id;
    private String nombre;
    private String genero;
    private Integer edad;
    private String identificacion;
    private String direccion;
    private String telefono;

    protected Persona(
            UUID id,
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono
    ) {
        this.id = Objects.requireNonNull(id, "El identificador de persona no puede ser nulo");
        setNombre(nombre);
        setGenero(genero);
        setEdad(edad);
        setIdentificacion(identificacion);
        setDireccion(direccion);
        setTelefono(telefono);
    }

    public final void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessValidationException("El nombre de la persona es obligatorio");
        }
        this.nombre = nombre.trim();
    }

    public final void setGenero(String genero) {
        this.genero = (genero == null || genero.isBlank()) ? "No especificado" : genero.trim();
    }

    public final void setEdad(Integer edad) {
        if (edad != null && (edad < 0 || edad > 130)) {
            throw new BusinessValidationException("La edad debe estar entre 0 y 130 años");
        }
        this.edad = edad;
    }

    public final void setIdentificacion(String identificacion) {
        this.identificacion = (identificacion == null || identificacion.isBlank()) ? null : identificacion.trim();
    }

    public final void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new BusinessValidationException("La dirección es obligatoria");
        }
        this.direccion = direccion.trim();
    }

    public final void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new BusinessValidationException("El teléfono es obligatorio");
        }
        this.telefono = telefono.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getGenero() {
        return genero;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }
}
