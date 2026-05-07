package uk.ac.qmul.digitalid.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DigitalIdTest {
    @Test
    void statusHistoryClosesPreviousEntryOnChange() {
        LocalDateTime t0 = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime t1 = t0.plusDays(1);

        DigitalId digitalId = createBaseId(t0);
        digitalId.changeStatus(Status.SUSPENDED, t1, "investigation");

        assertEquals(Status.SUSPENDED, digitalId.getCurrentStatus());
        List<StatusHistoryEntry> history = digitalId.getStatusHistory();
        assertEquals(2, history.size());
        assertEquals(Status.ACTIVE, history.get(0).getStatus());
        assertEquals(t1, history.get(0).getEffectiveTo());
        assertEquals(Status.SUSPENDED, history.get(1).getStatus());
        assertNotNull(history.get(1).getEffectiveFrom());
    }

    @Test
    void hasStatusDuringDetectsSuspensionPeriod() {
        LocalDateTime t0 = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime t1 = t0.plusDays(1);
        LocalDateTime t2 = t1.plusDays(2);

        DigitalId digitalId = createBaseId(t0);
        digitalId.changeStatus(Status.SUSPENDED, t1, "compliance");
        digitalId.changeStatus(Status.ACTIVE, t2, "resolved");

        assertTrue(digitalId.hasStatusDuring(Status.SUSPENDED, t1, t2));
        assertFalse(digitalId.hasStatusDuring(Status.REVOKED, t0, t2));
    }

    @Test
    void restrictionActiveAtReturnsExpectedValue() {
        LocalDateTime t0 = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime t1 = t0.plusDays(3);
        LocalDateTime t2 = t1.plusDays(2);

        DigitalId digitalId = createBaseId(t0);
        digitalId.addRestriction(new Restriction(
                RestrictionType.DRIVING_RESTRICTION,
                t1,
                t2,
                "court order"
        ));

        assertFalse(digitalId.hasRestrictionActiveAt(RestrictionType.DRIVING_RESTRICTION, t0));
        assertTrue(digitalId.hasRestrictionActiveAt(RestrictionType.DRIVING_RESTRICTION, t1.plusHours(1)));
    }

    private static DigitalId createBaseId(LocalDateTime effectiveFrom) {
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
                effectiveFrom,
                "created"
        );
    }
}
