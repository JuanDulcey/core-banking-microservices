package com.devv.banking.cuentas.domain.ports.out;

import com.devv.banking.cuentas.domain.model.ClienteSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteSnapshotRepositoryPort {

    ClienteSnapshot save(ClienteSnapshot snapshot);

    Optional<ClienteSnapshot> findById(UUID id);

    Optional<ClienteSnapshot> findByAnyIdentifier(String referencia);

    List<ClienteSnapshot> findAll();
}
