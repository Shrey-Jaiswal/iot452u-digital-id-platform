package uk.ac.qmul.digitalid.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.ResidencyStatus;
import uk.ac.qmul.digitalid.domain.Status;
import uk.ac.qmul.digitalid.persistence.DigitalIdRepository;

public final class IdentityManagementService {
    private final DigitalIdRepository repository;
    private final AuthorizationService authorizationService;

    public IdentityManagementService(DigitalIdRepository repository, AuthorizationService authorizationService) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.authorizationService = Objects.requireNonNull(authorizationService, "authorizationService");
    }

    public DigitalId createIdentity(
            OrganizationType actor,
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
        authorizationService.requireCentralAuthority(actor);
        ensureNotExists(id, nationalId);
        DigitalId digitalId = DigitalId.createActive(
                id,
                nationalId,
                dateOfBirth,
                placeOfBirth,
                fullName,
                address,
                phone,
                email,
                residencyStatus,
                effectiveFrom,
                reason
        );
        repository.save(digitalId);
        return digitalId;
    }

    public void updateContact(
            OrganizationType actor,
            UUID id,
            String fullName,
            String address,
            String phone,
            String email
    ) {
        authorizationService.requireCentralAuthority(actor);
        DigitalId digitalId = requireExisting(id);
        if (digitalId.getCurrentStatus() == Status.REVOKED) {
            throw new DomainValidationException("Cannot update a revoked Digital ID");
        }
        digitalId.updateContact(fullName, address, phone, email);
        repository.save(digitalId);
    }

    public void changeStatus(
            OrganizationType actor,
            UUID id,
            Status newStatus,
            LocalDateTime effectiveFrom,
            String reason
    ) {
        authorizationService.requireCentralAuthority(actor);
        DigitalId digitalId = requireExisting(id);
        if (digitalId.getCurrentStatus() == Status.REVOKED && newStatus != Status.REVOKED) {
            throw new DomainValidationException("Cannot change status after revocation");
        }
        digitalId.changeStatus(newStatus, effectiveFrom, reason);
        repository.save(digitalId);
    }

    public void updateResidencyStatus(
            OrganizationType actor,
            UUID id,
            ResidencyStatus residencyStatus
    ) {
        authorizationService.requireCentralAuthority(actor);
        DigitalId digitalId = requireExisting(id);
        if (digitalId.getCurrentStatus() == Status.REVOKED) {
            throw new DomainValidationException("Cannot update residency status on revoked ID");
        }
        digitalId.setResidencyStatus(residencyStatus);
        repository.save(digitalId);
    }

    private DigitalId requireExisting(UUID id) {
        Objects.requireNonNull(id, "id");
        Optional<DigitalId> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new DomainValidationException("Digital ID not found");
        }
        return existing.get();
    }

    private void ensureNotExists(UUID id, String nationalId) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(nationalId, "nationalId");
        if (repository.findById(id).isPresent()) {
            throw new DomainValidationException("Digital ID already exists");
        }
        if (repository.findByNationalId(nationalId).isPresent()) {
            throw new DomainValidationException("nationalId already exists");
        }
    }
}
