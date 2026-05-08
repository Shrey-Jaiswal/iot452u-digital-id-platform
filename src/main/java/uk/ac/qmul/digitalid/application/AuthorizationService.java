package uk.ac.qmul.digitalid.application;

import java.util.Objects;
import uk.ac.qmul.digitalid.domain.OrganizationType;

public final class AuthorizationService {
    public void requireCentralAuthority(OrganizationType actorType) {
        Objects.requireNonNull(actorType, "actorType");
        if (!actorType.isCentralAuthority()) {
            throw new AuthorizationException("Only CentralAuthority can mutate Digital IDs");
        }
    }
}
