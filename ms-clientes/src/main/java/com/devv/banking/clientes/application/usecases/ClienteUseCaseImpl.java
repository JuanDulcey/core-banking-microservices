package com.devv.banking.clientes.application.usecases;

import com.devv.banking.clientes.domain.exception.BusinessValidationException;
import com.devv.banking.clientes.domain.exception.DuplicateResourceException;
import com.devv.banking.clientes.domain.exception.ResourceNotFoundException;
import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.domain.model.TipoEventoCliente;
import com.devv.banking.clientes.domain.ports.in.ClienteUseCase;
import com.devv.banking.clientes.domain.ports.out.ClienteEventPublisherPort;
import com.devv.banking.clientes.domain.ports.out.ClienteRepositoryPort;
import com.devv.banking.clientes.domain.ports.out.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Orquestador de Casos de Uso de {@link Cliente} (Capa de Aplicación).
 * <p>
 * Coordina la validación de unicidad, el cifrado de contraseñas mediante {@link PasswordEncoderPort},
 * la persistencia a través de {@link ClienteRepositoryPort} y la publicación de eventos de dominio/integración.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ClienteUseCaseImpl implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final ClienteEventPublisherPort eventPublisher;

    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public Cliente crear(CrearClienteCommand command) {
        validarContrasenaTextoPlano(command.contrasena());

        if (command.clienteId() != null && !command.clienteId().isBlank()
                && clienteRepository.existsByClienteId(command.clienteId().trim())) {
            throw new DuplicateResourceException("Ya existe un cliente con el clienteId: " + command.clienteId().trim());
        }

        if (command.identificacion() != null && !command.identificacion().isBlank()
                && clienteRepository.existsByIdentificacion(command.identificacion().trim())) {
            throw new DuplicateResourceException("Ya existe un cliente con la identificación: " + command.identificacion().trim());
        }

        String passwordHash = passwordEncoder.encode(command.contrasena().trim());

        Cliente nuevoCliente = Cliente.crearNuevo(
                command.nombre(),
                command.genero(),
                command.edad(),
                command.identificacion(),
                command.direccion(),
                command.telefono(),
                command.clienteId(),
                passwordHash,
                command.estado()
        );

        Cliente guardado = clienteRepository.save(nuevoCliente);
        eventPublisher.publish(guardado, TipoEventoCliente.CREATED);
        return guardado;
    }

    @Override
    @Cacheable(value = "clientes", key = "#identificador")
    @Transactional(readOnly = true)
    public Cliente obtenerPorIdentificador(String identificador) {
        return resolverCliente(identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con identificador: " + identificador));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos(Boolean estado) {
        if (estado != null) {
            return clienteRepository.findByEstado(estado);
        }
        return clienteRepository.findAll();
    }

    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public Cliente actualizar(String identificador, ActualizarClienteCommand command) {
        Cliente existente = resolverCliente(identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con identificador: " + identificador));

        validarIdentificacionUnicaEnActualizacion(existente, command.identificacion());

        existente.actualizarDatos(
                command.nombre(),
                command.genero(),
                command.edad(),
                command.identificacion(),
                command.direccion(),
                command.telefono(),
                command.estado() != null ? command.estado() : existente.isEstado()
        );

        if (command.contrasena() != null && !command.contrasena().isBlank()) {
            validarContrasenaTextoPlano(command.contrasena());
            existente.cambiarContrasena(passwordEncoder.encode(command.contrasena().trim()));
        }

        Cliente actualizado = clienteRepository.save(existente);
        eventPublisher.publish(actualizado, TipoEventoCliente.UPDATED);
        return actualizado;
    }

    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public Cliente actualizarParcial(String identificador, ActualizarParcialClienteCommand command) {
        Cliente existente = resolverCliente(identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con identificador: " + identificador));

        if (command.identificacion() != null) {
            validarIdentificacionUnicaEnActualizacion(existente, command.identificacion());
            existente.setIdentificacion(command.identificacion());
        }
        if (command.nombre() != null) {
            existente.setNombre(command.nombre());
        }
        if (command.genero() != null) {
            existente.setGenero(command.genero());
        }
        if (command.edad() != null) {
            existente.setEdad(command.edad());
        }
        if (command.direccion() != null) {
            existente.setDireccion(command.direccion());
        }
        if (command.telefono() != null) {
            existente.setTelefono(command.telefono());
        }
        if (command.contrasena() != null && !command.contrasena().isBlank()) {
            validarContrasenaTextoPlano(command.contrasena());
            existente.cambiarContrasena(passwordEncoder.encode(command.contrasena().trim()));
        }
        if (command.estado() != null) {
            if (command.estado()) {
                existente.activar();
            } else {
                existente.desactivar();
            }
        }

        Cliente actualizado = clienteRepository.save(existente);
        eventPublisher.publish(actualizado, TipoEventoCliente.UPDATED);
        return actualizado;
    }

    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public void eliminar(String identificador) {
        Cliente existente = resolverCliente(identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con identificador: " + identificador));

        existente.desactivar();
        clienteRepository.deleteById(existente.getId());
        eventPublisher.publish(existente, TipoEventoCliente.DELETED);
    }

    private Optional<Cliente> resolverCliente(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw new BusinessValidationException("El identificador de búsqueda no puede ser vacío");
        }
        String trimmed = identificador.trim();
        try {
            UUID uuid = UUID.fromString(trimmed);
            Optional<Cliente> porUuid = clienteRepository.findById(uuid);
            if (porUuid.isPresent()) {
                return porUuid;
            }
        } catch (IllegalArgumentException ignored) {
            // No es UUID; continuar buscando por clienteId o identificación
        }
        return clienteRepository.findByClienteId(trimmed)
                .or(() -> clienteRepository.findByIdentificacion(trimmed));
    }

    private void validarIdentificacionUnicaEnActualizacion(Cliente existente, String nuevaIdentificacion) {
        if (nuevaIdentificacion != null && !nuevaIdentificacion.isBlank()) {
            String trimmed = nuevaIdentificacion.trim();
            if (!Objects.equals(existente.getIdentificacion(), trimmed)
                    && clienteRepository.existsByIdentificacion(trimmed)) {
                throw new DuplicateResourceException("Ya existe otro cliente con la identificación: " + trimmed);
            }
        }
    }

    private void validarContrasenaTextoPlano(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().length() < 4) {
            throw new BusinessValidationException("La contraseña debe tener al menos 4 caracteres");
        }
    }
}
