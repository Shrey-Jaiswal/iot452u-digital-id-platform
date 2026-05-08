package uk.ac.qmul.digitalid.rules;

import java.time.LocalDateTime;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;

public interface VerificationRule {
    OrganizationType supportedActor();

    VerificationResult verify(DigitalId digitalId, LocalDateTime requestedAt);
}
