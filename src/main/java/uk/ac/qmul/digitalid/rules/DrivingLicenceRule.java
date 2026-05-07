package uk.ac.qmul.digitalid.rules;

import java.time.LocalDateTime;
import java.util.Objects;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.RestrictionType;
import uk.ac.qmul.digitalid.domain.Status;

public final class DrivingLicenceRule implements VerificationRule {
    @Override
    public OrganizationType supportedActor() {
        return OrganizationType.DRIVING_LICENCE_AUTHORITY;
    }

    @Override
    public VerificationResult verify(DigitalId digitalId, LocalDateTime requestedAt) {
        Objects.requireNonNull(digitalId, "digitalId");
        Objects.requireNonNull(requestedAt, "requestedAt");
        if (!digitalId.isStatusAt(Status.ACTIVE, requestedAt)) {
            return VerificationResult.deny("Digital ID is not active");
        }
        if (digitalId.hasRestrictionActiveAt(RestrictionType.DRIVING_RESTRICTION, requestedAt)) {
            return VerificationResult.deny("Driving restriction is active");
        }
        return VerificationResult.allow();
    }
}
