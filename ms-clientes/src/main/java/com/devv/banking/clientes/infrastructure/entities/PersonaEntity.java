package com.devv.banking.clientes.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad JPA base mapeada a la tabla {@code personas}.
 * <p>
 * Utiliza {@link InheritanceType#JOINED} para mantener un modelo relacional normalizado (3FN),
 * donde {@link ClienteEntity} comparte la clave primaria {@code persona_id} mediante clave foránea.
 */
@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class PersonaEntity {

    @Id
    @Column(name = "persona_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "genero", nullable = false, length = 30)
    private String genero;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "identificacion", unique = true, length = 30)
    private String identificacion;

    @Column(name = "direccion", nullable = false, length = 250)
    private String direccion;

    @Column(name = "telefono", nullable = false, length = 30)
    private String telefono;
}
