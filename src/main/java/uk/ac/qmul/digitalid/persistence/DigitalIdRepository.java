package uk.ac.qmul.digitalid.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import uk.ac.qmul.digitalid.domain.DigitalId;

public interface DigitalIdRepository {
    void save(DigitalId digitalId);

    Optional<DigitalId> findById(UUID id);

    Optional<DigitalId> findByNationalId(String nationalId);

    List<DigitalId> findAll();
}
