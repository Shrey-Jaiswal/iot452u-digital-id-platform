package uk.ac.qmul.digitalid.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import uk.ac.qmul.digitalid.domain.DigitalId;

public final class InMemoryDigitalIdRepository implements DigitalIdRepository {
    private final Map<UUID, DigitalId> store = new HashMap<>();
    private final Map<String, UUID> nationalIdIndex = new HashMap<>();

    @Override
    public synchronized void save(DigitalId digitalId) {
        Objects.requireNonNull(digitalId, "digitalId");
        UUID id = digitalId.getId();
        String nationalId = digitalId.getNationalId();
        UUID existingId = nationalIdIndex.get(nationalId);
        if (existingId != null && !existingId.equals(id)) {
            throw new IllegalArgumentException("nationalId already exists");
        }
        store.put(id, digitalId);
        nationalIdIndex.put(nationalId, id);
    }

    @Override
    public synchronized Optional<DigitalId> findById(UUID id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public synchronized Optional<DigitalId> findByNationalId(String nationalId) {
        Objects.requireNonNull(nationalId, "nationalId");
        UUID id = nationalIdIndex.get(nationalId);
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public synchronized List<DigitalId> findAll() {
        return new ArrayList<>(store.values());
    }
}
