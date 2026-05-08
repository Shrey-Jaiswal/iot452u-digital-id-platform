package uk.ac.qmul.digitalid.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Restriction {
    private final RestrictionType type;
    private final LocalDateTime effectiveFrom;
    private final LocalDateTime effectiveTo;
    private final String reason;

    public Restriction(RestrictionType type, LocalDateTime effectiveFrom, LocalDateTime effectiveTo, String reason) {
        this.type = Objects.requireNonNull(type, "type");
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        this.reason = requireNonBlank(reason, "reason");
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("effectiveTo must be after effectiveFrom");
        }
        this.effectiveTo = effectiveTo;
    }

    public RestrictionType getType() {
        return type;
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
