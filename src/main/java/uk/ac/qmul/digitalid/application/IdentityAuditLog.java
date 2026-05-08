package uk.ac.qmul.digitalid.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class IdentityAuditLog {
    private final List<IdentityAuditEvent> events = new ArrayList<>();

    public void record(IdentityAuditEvent event) {
        events.add(Objects.requireNonNull(event, "event"));
    }

    public List<IdentityAuditEvent> list() {
        return Collections.unmodifiableList(events);
    }
}
