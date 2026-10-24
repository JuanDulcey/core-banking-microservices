package com.devv.banking.clientes.infrastructure.adapters;

import com.devv.banking.clientes.domain.model.Cliente;
import com.devv.banking.clientes.domain.ports.out.ClienteRepositoryPort;
import com.devv.banking.clientes.infrastructure.repositories.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador Secundario de Persistencia (Driven Adapter): implementa {@link ClienteRepositoryPort}
 * delegando el acceso a datos en Spring Data JPA y traduciendo objetos con {@link ClientePersistenceMapper}.
 */
@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;
    private final ClientePersistenceMapper mapper;

    @Override
    public Cliente save(Cliente cliente) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cliente)));
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> findByClienteId(String clienteId) {
        return jpaRepository.findByClienteIdIgnoreCase(clienteId).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> findByIdentificacion(String identificacion) {
        return jpaRepository.findByIdentificacion(identificacion).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Cliente> findByEstado(boolean estado) {
        return jpaRepository.findByEstado(estado).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByClienteId(String clienteId) {
        return jpaRepository.existsByClienteIdIgnoreCase(clienteId);
    }

    @Override
    public boolean existsByIdentificacion(String identificacion) {
        return jpaRepository.existsByIdentificacion(identificacion);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
