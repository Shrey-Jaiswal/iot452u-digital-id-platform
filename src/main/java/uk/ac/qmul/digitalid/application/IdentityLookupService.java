package uk.ac.qmul.digitalid.application;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.persistence.DigitalIdRepository;

public final class IdentityLookupService {
    private final DigitalIdRepository repository;

    public IdentityLookupService(DigitalIdRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Optional<DigitalId> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<DigitalId> findByNationalId(String nationalId) {
        return repository.findByNationalId(nationalId);
    }
}
