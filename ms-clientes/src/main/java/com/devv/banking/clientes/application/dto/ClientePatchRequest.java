package com.devv.banking.clientes.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualización parcial de un cliente (PATCH)")
public record ClientePatchRequest(

        @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
        @JsonAlias({"nombres", "name"})
        String nombre,

        @Size(max = 30, message = "El género no debe exceder 30 caracteres")
        @JsonAlias({"género", "gender"})
        String genero,

        @Min(value = 0, message = "La edad no puede ser negativa")
        @Max(value = 130, message = "La edad no debe superar 130 años")
        Integer edad,

        @Size(max = 30, message = "La identificación no debe exceder 30 caracteres")
        @JsonAlias({"identificación", "dni", "cedula"})
        String identificacion,

        @Size(max = 250, message = "La dirección no debe exceder 250 caracteres")
        @JsonAlias({"dirección", "address"})
        String direccion,

        @Size(max = 30, message = "El teléfono no debe exceder 30 caracteres")
        @JsonAlias({"teléfono", "phone"})
        String telefono,

        @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
        @JsonAlias({"contraseña", "password"})
        String contrasena,

        Boolean estado
) {}
