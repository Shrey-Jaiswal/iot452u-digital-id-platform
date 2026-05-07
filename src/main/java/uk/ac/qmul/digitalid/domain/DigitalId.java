package uk.ac.qmul.digitalid.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class DigitalId {
    private final UUID id;
    private final String nationalId;
    private final LocalDate dateOfBirth;
    private final String placeOfBirth;

    private String fullName;
    private String address;
    private String phone;
    private String email;

    private Status currentStatus;
    private final List<StatusHistoryEntry> statusHistory;
    private final List<Restriction> restrictions;
    private ResidencyStatus residencyStatus;

    private DigitalId(
            UUID id,
            String nationalId,
            LocalDate dateOfBirth,
            String placeOfBirth,
            String fullName,
            String address,
            String phone,
            String email,
            Status initialStatus,
            ResidencyStatus residencyStatus,
            LocalDateTime effectiveFrom,
            String reason
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.nationalId = requireNonBlank(nationalId, "nationalId");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "dateOfBirth");
        this.placeOfBirth = requireNonBlank(placeOfBirth, "placeOfBirth");
        this.fullName = requireNonBlank(fullName, "fullName");
        this.address = requireNonBlank(address, "address");
        this.phone = requireNonBlank(phone, "phone");
        this.email = requireNonBlank(email, "email");
        this.currentStatus = Objects.requireNonNull(initialStatus, "initialStatus");
        this.residencyStatus = Objects.requireNonNull(residencyStatus, "residencyStatus");

        this.statusHistory = new ArrayList<>();
        this.statusHistory.add(new StatusHistoryEntry(initialStatus, effectiveFrom, reason));
        this.restrictions = new ArrayList<>();
    }

    public static DigitalId create(
            UUID id,
            String nationalId,
            LocalDate dateOfBirth,
            String placeOfBirth,
            String fullName,
            String address,
            String phone,
            String email,
            Status initialStatus,
            ResidencyStatus residencyStatus,
            LocalDateTime effectiveFrom,
            String reason
    ) {
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        return new DigitalId(
                id,
                nationalId,
                dateOfBirth,
                placeOfBirth,
                fullName,
                address,
                phone,
                email,
                initialStatus,
                residencyStatus,
                effectiveFrom,
                reason
        );
    }

    public static DigitalId createActive(
            UUID id,
            String nationalId,
            LocalDate dateOfBirth,
            String placeOfBirth,
            String fullName,
            String address,
            String phone,
            String email,
            ResidencyStatus residencyStatus,
            LocalDateTime effectiveFrom,
            String reason
    ) {
        return create(
                id,
                nationalId,
                dateOfBirth,
                placeOfBirth,
                fullName,
                address,
                phone,
                email,
                Status.ACTIVE,
                residencyStatus,
                effectiveFrom,
                reason
        );
    }

    public UUID getId() {
        return id;
    }

    public String getNationalId() {
        return nationalId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public ResidencyStatus getResidencyStatus() {
        return residencyStatus;
    }

    public List<StatusHistoryEntry> getStatusHistory() {
        return Collections.unmodifiableList(statusHistory);
    }

    public List<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    public void updateContact(String fullName, String address, String phone, String email) {
        this.fullName = requireNonBlank(fullName, "fullName");
        this.address = requireNonBlank(address, "address");
        this.phone = requireNonBlank(phone, "phone");
        this.email = requireNonBlank(email, "email");
    }

    public void changeStatus(Status newStatus, LocalDateTime effectiveFrom, String reason) {
        Objects.requireNonNull(newStatus, "newStatus");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        if (newStatus == currentStatus) {
            return;
        }
        StatusHistoryEntry latest = statusHistory.get(statusHistory.size() - 1);
        if (effectiveFrom.isBefore(latest.getEffectiveFrom())) {
            throw new IllegalArgumentException("effectiveFrom must be after current status start");
        }
        if (latest.getEffectiveTo() == null) {
            latest.closeAt(effectiveFrom);
        }
        statusHistory.add(new StatusHistoryEntry(newStatus, effectiveFrom, reason));
        currentStatus = newStatus;
    }

    public void addRestriction(Restriction restriction) {
        restrictions.add(Objects.requireNonNull(restriction, "restriction"));
    }

    public void setResidencyStatus(ResidencyStatus residencyStatus) {
        this.residencyStatus = Objects.requireNonNull(residencyStatus, "residencyStatus");
    }

    public boolean hasRestrictionActiveAt(RestrictionType type, LocalDateTime instant) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(instant, "instant");
        for (Restriction restriction : restrictions) {
            if (restriction.getType() == type && restriction.isActiveAt(instant)) {
                return true;
            }
        }
        return false;
    }

    public boolean isStatusAt(Status status, LocalDateTime instant) {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(instant, "instant");
        for (StatusHistoryEntry entry : statusHistory) {
            if (entry.getStatus() == status && entry.isActiveAt(instant)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasStatusDuring(Status status, LocalDateTime from, LocalDateTime to) {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        for (StatusHistoryEntry entry : statusHistory) {
            if (entry.getStatus() == status && entry.overlaps(from, to)) {
                return true;
            }
        }
        return false;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must be provided");
        }
        return value;
    }
}
