package uk.ac.qmul.digitalid.application;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import uk.ac.qmul.digitalid.domain.Status;

public final class StatusTransitionPolicy {
    private final Set<Status> fromActive = EnumSet.of(Status.SUSPENDED, Status.REVOKED);
    private final Set<Status> fromSuspended = EnumSet.of(Status.ACTIVE, Status.REVOKED);
    private final Set<Status> fromRevoked = EnumSet.of(Status.REVOKED);

    public void validate(Status from, Status to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (from == Status.ACTIVE && !fromActive.contains(to)) {
            throw new DomainValidationException("Invalid transition from ACTIVE to " + to);
        }
        if (from == Status.SUSPENDED && !fromSuspended.contains(to)) {
            throw new DomainValidationException("Invalid transition from SUSPENDED to " + to);
        }
        if (from == Status.REVOKED && !fromRevoked.contains(to)) {
            throw new DomainValidationException("Invalid transition from REVOKED to " + to);
        }
    }
}
