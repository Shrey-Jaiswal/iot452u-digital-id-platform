package uk.ac.qmul.digitalid.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public final class StatusHistoryEntry {
    private final Status status;
    private final LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private final String reason;

    public StatusHistoryEntry(Status status, LocalDateTime effectiveFrom, String reason) {
        this(status, effectiveFrom, null, reason);
    }

    public StatusHistoryEntry(Status status, LocalDateTime effectiveFrom, LocalDateTime effectiveTo, String reason) {
        this.status = Objects.requireNonNull(status, "status");
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        this.reason = requireNonBlank(reason, "reason");
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("effectiveTo must be after effectiveFrom");
        }
        this.effectiveTo = effectiveTo;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public String getReason() {
        return reason;
    }

    public void closeAt(LocalDateTime end) {
        Objects.requireNonNull(end, "end");
        if (effectiveTo != null) {
            throw new IllegalStateException("Status history entry already closed");
        }
        if (end.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("end must be after effectiveFrom");
        }
        effectiveTo = end;
    }

    public boolean isActiveAt(LocalDateTime instant) {
        Objects.requireNonNull(instant, "instant");
        if (instant.isBefore(effectiveFrom)) {
            return false;
        }
        return effectiveTo == null || !instant.isAfter(effectiveTo);
    }

    public boolean overlaps(LocalDateTime from, LocalDateTime to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("to must be after from");
        }
        LocalDateTime end = effectiveTo == null ? LocalDateTime.MAX : effectiveTo;
        return !end.isBefore(from) && !effectiveFrom.isAfter(to);
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must be provided");
        }
        return value;
    }
}
