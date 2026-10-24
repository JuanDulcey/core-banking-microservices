package com.devv.banking.clientes.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualización completa de un cliente (PUT)")
public record ClienteUpdateRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
        @JsonAlias({"nombres", "name"})
        @Schema(example = "Jose Lema")
        String nombre,

        @Size(max = 30, message = "El género no debe exceder 30 caracteres")
        @JsonAlias({"género", "gender"})
        @Schema(example = "Masculino")
        String genero,

        @Min(value = 0, message = "La edad no puede ser negativa")
        @Max(value = 130, message = "La edad no debe superar 130 años")
        @Schema(example = "33")
        Integer edad,

        @Size(max = 30, message = "La identificación no debe exceder 30 caracteres")
        @JsonAlias({"identificación", "dni", "cedula"})
        @Schema(example = "1710012345")
        String identificacion,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 250, message = "La dirección no debe exceder 250 caracteres")
        @JsonAlias({"dirección", "address"})
        @Schema(example = "Otavalo sn y principal")
        String direccion,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 30, message = "El teléfono no debe exceder 30 caracteres")
        @JsonAlias({"teléfono", "phone"})
        @Schema(example = "098254785")
        String telefono,

        @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
        @JsonAlias({"contraseña", "password"})
        @Schema(example = "1234", description = "Opcional en actualización; si se envía, se re-encripta")
        String contrasena,

        @NotNull(message = "El estado es obligatorio en actualización completa")
        @Schema(example = "true")
        Boolean estado
) {}
