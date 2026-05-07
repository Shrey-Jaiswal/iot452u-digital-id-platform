package uk.ac.qmul.digitalid.application;

import java.time.LocalDateTime;
import java.util.Objects;
import uk.ac.qmul.digitalid.domain.OrganizationType;

public final class IdentityRequestContext {
    private final OrganizationType actor;
    private final LocalDateTime requestedAt;

    public IdentityRequestContext(OrganizationType actor, LocalDateTime requestedAt) {
        this.actor = Objects.requireNonNull(actor, "actor");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt");
    }

    public OrganizationType getActor() {
        return actor;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
}
