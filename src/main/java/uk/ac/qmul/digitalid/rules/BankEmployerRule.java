package uk.ac.qmul.digitalid.rules;

import java.time.LocalDateTime;
import java.util.Objects;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.Status;

public final class BankEmployerRule implements VerificationRule {
    @Override
    public OrganizationType supportedActor() {
        return OrganizationType.BANK_EMPLOYER;
    }

    @Override
    public VerificationResult verify(DigitalId digitalId, LocalDateTime requestedAt) {
        Objects.requireNonNull(digitalId, "digitalId");
        Objects.requireNonNull(requestedAt, "requestedAt");
        return digitalId.isStatusAt(Status.ACTIVE, requestedAt)
                ? VerificationResult.allow()
                : VerificationResult.deny("Digital ID is not active");
    }
}
