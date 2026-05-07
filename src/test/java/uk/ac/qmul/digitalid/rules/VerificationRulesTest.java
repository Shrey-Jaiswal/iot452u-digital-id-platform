package uk.ac.qmul.digitalid.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.ResidencyStatus;
import uk.ac.qmul.digitalid.domain.Restriction;
import uk.ac.qmul.digitalid.domain.RestrictionType;
import uk.ac.qmul.digitalid.domain.Status;

class VerificationRulesTest {
    @Test
    void bankEmployerAllowsActive() {
        LocalDateTime t0 = LocalDateTime.of(2025, 1, 1, 10, 0);
        DigitalId digitalId = createBaseId(t0);

        VerificationResult result = new BankEmployerRule().verify(digitalId, t0.plusHours(2));
        assertTrue(result.isAllowed());
    }

    @Test
    void bankEmployerDeniesWhenNotActive() {
        LocalDateTime t0 = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime t1 = t0.plusDays(1);
        DigitalId digitalId = createBaseId(t0);
        digitalId.changeStatus(Status.SUSPENDED, t1, "hold");

        VerificationResult result = new BankEmployerRule().verify(digitalId, t1.plusHours(1));
        assertFalse(result.isAllowed());
    }

    @Test
    void drivingRuleDeniesWhenRestrictionActive() {
        LocalDateTime t0 = LocalDateTime.of(2025, 2, 1, 9, 0);
        LocalDateTime t1 = t0.plusDays(1);
        DigitalId digitalId = createBaseId(t0);
        digitalId.addRestriction(new Restriction(
                RestrictionType.DRIVING_RESTRICTION,
                t1,
                t1.plusDays(3),
                "medical"
        ));

        VerificationResult result = new DrivingLicenceRule().verify(digitalId, t1.plusHours(2));
        assertFalse(result.isAllowed());
    }

    @Test
    void drivingRuleAllowsWhenNoRestriction() {
        LocalDateTime t0 = LocalDateTime.of(2025, 2, 1, 9, 0);
        DigitalId digitalId = createBaseId(t0);

        VerificationResult result = new DrivingLicenceRule().verify(digitalId, t0.plusHours(1));
        assertTrue(result.isAllowed());
    }

    @Test
    void immigrationRuleRequiresValidResidency() {
        LocalDateTime t0 = LocalDateTime.of(2025, 3, 1, 8, 0);
        DigitalId digitalId = createBaseId(t0);
        digitalId.setResidencyStatus(ResidencyStatus.EXPIRED);

        VerificationResult result = new ImmigrationRule().verify(digitalId, t0.plusHours(1));
        assertFalse(result.isAllowed());
    }

    @Test
    void immigrationRuleDeniesWhenHoldActive() {
        LocalDateTime t0 = LocalDateTime.of(2025, 3, 1, 8, 0);
        LocalDateTime t1 = t0.plusDays(1);
        DigitalId digitalId = createBaseId(t0);
        digitalId.addRestriction(new Restriction(
                RestrictionType.IMMIGRATION_HOLD,
                t1,
                t1.plusDays(2),
                "review"
        ));

        VerificationResult result = new ImmigrationRule().verify(digitalId, t1.plusHours(3));
        assertFalse(result.isAllowed());
    }

    @Test
    void taxRuleDeniesIfSuspendedDuringPeriod() {
        LocalDateTime t0 = LocalDateTime.of(2025, 4, 1, 9, 0);
        LocalDateTime t1 = t0.plusDays(2);
        LocalDateTime t2 = t1.plusDays(2);
        DigitalId digitalId = createBaseId(t0);
        digitalId.changeStatus(Status.SUSPENDED, t1, "audit");
        digitalId.changeStatus(Status.ACTIVE, t2, "cleared");

        TaxAuthorityRule rule = new TaxAuthorityRule(t0, t2);
        VerificationResult result = rule.verify(digitalId, t2.plusHours(1));
        assertFalse(result.isAllowed());
    }

    @Test
    void taxRuleAllowsWhenNoSuspensionDuringPeriod() {
        LocalDateTime t0 = LocalDateTime.of(2025, 4, 1, 9, 0);
        LocalDateTime t1 = t0.plusDays(2);
        DigitalId digitalId = createBaseId(t0);

        TaxAuthorityRule rule = new TaxAuthorityRule(t0, t1);
        VerificationResult result = rule.verify(digitalId, t1.minusHours(1));
        assertTrue(result.isAllowed());
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
