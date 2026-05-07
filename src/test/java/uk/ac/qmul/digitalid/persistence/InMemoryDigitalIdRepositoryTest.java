package uk.ac.qmul.digitalid.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.ResidencyStatus;

class InMemoryDigitalIdRepositoryTest {
    @Test
    void savesAndFindsById() {
        InMemoryDigitalIdRepository repository = new InMemoryDigitalIdRepository();
        DigitalId digitalId = createBaseId();

        repository.save(digitalId);

        assertTrue(repository.findById(digitalId.getId()).isPresent());
        assertEquals(digitalId.getNationalId(), repository.findById(digitalId.getId()).get().getNationalId());
    }

    @Test
    void findsByNationalId() {
        InMemoryDigitalIdRepository repository = new InMemoryDigitalIdRepository();
        DigitalId digitalId = createBaseId();
        repository.save(digitalId);

        assertTrue(repository.findByNationalId(digitalId.getNationalId()).isPresent());
    }

    @Test
    void rejectsDuplicateNationalId() {
        InMemoryDigitalIdRepository repository = new InMemoryDigitalIdRepository();
        DigitalId first = createBaseId();
        DigitalId second = DigitalId.createActive(
                UUID.randomUUID(),
                first.getNationalId(),
                LocalDate.of(1991, 2, 2),
                "Manchester",
                "Other Person",
                "2 Sample Road",
                "0700000000",
                "other@example.com",
                ResidencyStatus.VALID,
                LocalDateTime.now(),
                "create"
        );

        repository.save(first);
        assertThrows(IllegalArgumentException.class, () -> repository.save(second));
    }

    @Test
    void returnsEmptyForMissingId() {
        InMemoryDigitalIdRepository repository = new InMemoryDigitalIdRepository();
        assertFalse(repository.findById(UUID.randomUUID()).isPresent());
    }

    private static DigitalId createBaseId() {
        return DigitalId.createActive(
                UUID.randomUUID(),
                "NAT-" + UUID.randomUUID(),
                LocalDate.of(1990, 1, 1),
                "London",
                "Test Person",
                "1 Sample Road",
                "0123456789",
                "test@example.com",
                ResidencyStatus.VALID,
                LocalDateTime.now(),
                "created"
        );
    }
}
