package com.devv.banking.clientes.domain.model;

import com.devv.banking.clientes.domain.exception.BusinessValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * F5: Pruebas unitarias puras para la entidad de dominio Cliente y su herencia de Persona.
 */
class ClienteDomainTest {

    @Test
    @DisplayName("Debe crear un Cliente válido heredando atributos de Persona y generando clienteId")
    void debeCrearClienteValidoConHerenciaPersona() {
        Cliente cliente = Cliente.crearNuevo(
                "Jose Lema",
                "Masculino",
                32,
                "1710012345",
                "Otavalo sn y principal",
                "098254785",
                null,
                "$2a$10$hashedPasswordValue",
                true
        );

        assertThat(cliente).isInstanceOf(Persona.class);
        assertThat(cliente.getId()).isNotNull();
        assertThat(cliente.getClienteId()).startsWith("CLI-");
        assertThat(cliente.getNombre()).isEqualTo("Jose Lema");
        assertThat(cliente.getGenero()).isEqualTo("Masculino");
        assertThat(cliente.getEdad()).isEqualTo(32);
        assertThat(cliente.getIdentificacion()).isEqualTo("1710012345");
        assertThat(cliente.getDireccion()).isEqualTo("Otavalo sn y principal");
        assertThat(cliente.getTelefono()).isEqualTo("098254785");
        assertThat(cliente.isEstado()).isTrue();
        assertThat(cliente.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe asignar género por defecto 'No especificado' y estado true cuando se omiten")
    void debeAsignarValoresPorDefecto() {
        Cliente cliente = Cliente.crearNuevo(
                "Marianela Montalvo",
                null,
                null,
                null,
                "Amazonas y NNUU",
                "097548965",
                "CLI-MMONTALVO",
                "$2a$10$hash",
                null
        );

        assertThat(cliente.getGenero()).isEqualTo("No especificado");
        assertThat(cliente.isEstado()).isTrue();
        assertThat(cliente.getClienteId()).isEqualTo("CLI-MMONTALVO");
    }

    @Test
    @DisplayName("Debe rechazar la creación de Cliente cuando el nombre, dirección o teléfono están vacíos")
    void debeRechazarCamposObligatoriosVacios() {
        assertThatThrownBy(() -> Cliente.crearNuevo(
                "   ", "Masculino", 25, "123", "Dir", "099", "CLI-1", "hash", true
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("nombre");

        assertThatThrownBy(() -> Cliente.crearNuevo(
                "Juan Osorio", "Masculino", 25, "123", "", "099", "CLI-1", "hash", true
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("dirección");

        assertThatThrownBy(() -> Cliente.crearNuevo(
                "Juan Osorio", "Masculino", 25, "123", "13 junio y Equinoccial", " ", "CLI-1", "hash", true
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("teléfono");
    }

    @Test
    @DisplayName("Debe validar que la edad esté dentro de un rango humano razonable (0 a 130)")
    void debeValidarRangoEdad() {
        assertThatThrownBy(() -> Cliente.crearNuevo(
                "Jose Lema", "Masculino", -5, "123", "Otavalo", "098", "CLI-1", "hash", true
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("edad");

        assertThatThrownBy(() -> Cliente.crearNuevo(
                "Jose Lema", "Masculino", 150, "123", "Otavalo", "098", "CLI-1", "hash", true
        ))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("edad");
    }

    @Test
    @DisplayName("Debe permitir activar, desactivar y actualizar datos del Cliente")
    void debeActualizarEstadoYDatosCliente() {
        Cliente cliente = Cliente.crearNuevo(
                "Juan Osorio",
                "Masculino",
                28,
                "1720099887",
                "13 junio y Equinoccial",
                "098874587",
                "CLI-JOSORIO",
                "hashInicial",
                true
        );

        cliente.desactivar();
        assertThat(cliente.isEstado()).isFalse();

        cliente.activar();
        assertThat(cliente.isEstado()).isTrue();

        cliente.actualizarDatos(
                "Juan Osorio Editado",
                "Masculino",
                29,
                "1720099887",
                "Nueva Direccion 123",
                "099999999",
                false
        );

        assertThat(cliente.getNombre()).isEqualTo("Juan Osorio Editado");
        assertThat(cliente.getEdad()).isEqualTo(29);
        assertThat(cliente.getDireccion()).isEqualTo("Nueva Direccion 123");
        assertThat(cliente.isEstado()).isFalse();
    }
}
