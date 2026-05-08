package uk.ac.qmul.digitalid.application;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import uk.ac.qmul.digitalid.domain.OrganizationType;

public final class IdentityAuditEvent {
    private final UUID id;
    private final OrganizationType actor;
    private final String action;
    private final LocalDateTime occurredAt;

    public IdentityAuditEvent(UUID id, OrganizationType actor, String action, LocalDateTime occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.actor = Objects.requireNonNull(actor, "actor");
        this.action = requireNonBlank(action, "action");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public UUID getId() {
        return id;
    }

    public OrganizationType getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must be provided");
        }
        return value;
    }
}
