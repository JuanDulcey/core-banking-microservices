package com.devv.banking.cuentas.infrastructure.adapters;

import com.devv.banking.cuentas.domain.model.ClienteSnapshot;
import com.devv.banking.cuentas.domain.ports.out.ClienteSnapshotRepositoryPort;
import com.devv.banking.cuentas.infrastructure.repositories.ClienteSnapshotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteSnapshotRepositoryAdapter implements ClienteSnapshotRepositoryPort {

    private final ClienteSnapshotJpaRepository jpaRepository;
    private final ClienteSnapshotMapper mapper;

    @Override
    public ClienteSnapshot save(ClienteSnapshot snapshot) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(snapshot)));
    }

    @Override
    public Optional<ClienteSnapshot> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ClienteSnapshot> findByAnyIdentifier(String referencia) {
        if (referencia == null || referencia.isBlank()) {
            return Optional.empty();
        }
        String trimmed = referencia.trim();
        try {
            UUID uuid = UUID.fromString(trimmed);
            Optional<ClienteSnapshot> porUuid = jpaRepository.findById(uuid).map(mapper::toDomain);
            if (porUuid.isPresent()) {
                return porUuid;
            }
        } catch (IllegalArgumentException ignored) {
            // No es UUID; buscar por clienteId, identificación o nombre
        }

        return jpaRepository.findByClienteIdIgnoreCase(trimmed)
                .or(() -> jpaRepository.findByIdentificacion(trimmed))
                .or(() -> jpaRepository.findFirstByNombreIgnoreCase(trimmed))
                .map(mapper::toDomain);
    }

    @Override
    public List<ClienteSnapshot> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
