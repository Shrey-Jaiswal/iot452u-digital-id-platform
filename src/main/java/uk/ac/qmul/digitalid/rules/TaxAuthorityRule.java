package uk.ac.qmul.digitalid.rules;

import java.time.LocalDateTime;
import java.util.Objects;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.Status;

public final class TaxAuthorityRule implements VerificationRule {
    private final LocalDateTime periodStart;
    private final LocalDateTime periodEnd;

    public TaxAuthorityRule(LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.periodStart = Objects.requireNonNull(periodStart, "periodStart");
        this.periodEnd = Objects.requireNonNull(periodEnd, "periodEnd");
        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("periodEnd must be after periodStart");
        }
    }

    @Override
    public OrganizationType supportedActor() {
        return OrganizationType.TAX_AUTHORITY;
    }

    @Override
    public VerificationResult verify(DigitalId digitalId, LocalDateTime requestedAt) {
        Objects.requireNonNull(digitalId, "digitalId");
        Objects.requireNonNull(requestedAt, "requestedAt");
        if (!digitalId.isStatusAt(Status.ACTIVE, requestedAt)) {
            return VerificationResult.deny("Digital ID is not active");
        }
        if (digitalId.hasStatusDuring(Status.SUSPENDED, periodStart, periodEnd)) {
            return VerificationResult.deny("Digital ID was suspended during the reporting period");
        }
        return VerificationResult.allow();
    }
}
