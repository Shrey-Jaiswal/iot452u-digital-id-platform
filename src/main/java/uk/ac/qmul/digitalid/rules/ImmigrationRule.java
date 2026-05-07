package uk.ac.qmul.digitalid.rules;

import java.time.LocalDateTime;
import java.util.Objects;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.ResidencyStatus;
import uk.ac.qmul.digitalid.domain.RestrictionType;
import uk.ac.qmul.digitalid.domain.Status;

public final class ImmigrationRule implements VerificationRule {
    @Override
    public OrganizationType supportedActor() {
        return OrganizationType.IMMIGRATION_AUTHORITY;
    }

    @Override
    public VerificationResult verify(DigitalId digitalId, LocalDateTime requestedAt) {
        Objects.requireNonNull(digitalId, "digitalId");
        Objects.requireNonNull(requestedAt, "requestedAt");
        if (!digitalId.isStatusAt(Status.ACTIVE, requestedAt)) {
            return VerificationResult.deny("Digital ID is not active");
        }
        if (digitalId.getResidencyStatus() != ResidencyStatus.VALID) {
            return VerificationResult.deny("Residency status is not valid");
        }
        if (digitalId.hasRestrictionActiveAt(RestrictionType.IMMIGRATION_HOLD, requestedAt)) {
            return VerificationResult.deny("Immigration hold is active");
        }
        return VerificationResult.allow();
    }
}
