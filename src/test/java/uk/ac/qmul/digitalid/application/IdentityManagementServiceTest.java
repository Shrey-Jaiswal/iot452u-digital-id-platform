package uk.ac.qmul.digitalid.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.ResidencyStatus;
import uk.ac.qmul.digitalid.domain.Restriction;
import uk.ac.qmul.digitalid.domain.RestrictionType;
import uk.ac.qmul.digitalid.domain.Status;
import uk.ac.qmul.digitalid.persistence.InMemoryDigitalIdRepository;

class IdentityManagementServiceTest {
    private InMemoryDigitalIdRepository repository;
    private IdentityManagementService service;
    private UUID id;

    @BeforeEach
    void setup() {
        repository = new InMemoryDigitalIdRepository();
        service = new IdentityManagementService(repository, new AuthorizationService());
        id = UUID.randomUUID();
    }

    @Test
    void createsIdentityAndStoresIt() {
        service.createIdentity(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                "NAT-001",
                LocalDate.of(1990, 1, 1),
                "London",
                "A. Citizen",
                "10 Sample Street",
                "07123456789",
                "citizen@example.com",
                ResidencyStatus.VALID,
                LocalDateTime.now(),
                "create"
        );

        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    void rejectsDuplicateNationalId() {
        service.createIdentity(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                "NAT-001",
                LocalDate.of(1990, 1, 1),
                "London",
                "A. Citizen",
                "10 Sample Street",
                "07123456789",
                "citizen@example.com",
                ResidencyStatus.VALID,
                LocalDateTime.now(),
                "create"
        );

        assertThrows(DomainValidationException.class, () ->
                service.createIdentity(
                        OrganizationType.CENTRAL_AUTHORITY,
                        UUID.randomUUID(),
                        "NAT-001",
                        LocalDate.of(1991, 2, 2),
                        "Leeds",
                        "B. Citizen",
                        "11 Sample Street",
                        "07111111111",
                        "other@example.com",
                        ResidencyStatus.VALID,
                        LocalDateTime.now(),
                        "create"
                )
        );
    }

    @Test
    void rejectsUpdateOnRevokedId() {
        seedIdentity();
        service.changeStatus(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                Status.REVOKED,
                LocalDateTime.now(),
                "fraud"
        );

        assertThrows(DomainValidationException.class, () ->
                service.updateContact(
                        OrganizationType.CENTRAL_AUTHORITY,
                        id,
                        "Updated",
                        "New Address",
                        "07123456789",
                        "citizen@example.com"
                )
        );
    }

    @Test
    void rejectsResidencyUpdateOnRevokedId() {
        seedIdentity();
        service.changeStatus(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                Status.REVOKED,
                LocalDateTime.now(),
                "fraud"
        );

        assertThrows(DomainValidationException.class, () ->
                service.updateResidencyStatus(
                        OrganizationType.CENTRAL_AUTHORITY,
                        id,
                        ResidencyStatus.EXPIRED
                )
        );
    }

    @Test
    void rejectsRestrictionOnRevokedId() {
        seedIdentity();
        service.changeStatus(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                Status.REVOKED,
                LocalDateTime.now(),
                "fraud"
        );

        assertThrows(DomainValidationException.class, () ->
                service.addRestriction(
                        OrganizationType.CENTRAL_AUTHORITY,
                        id,
                        new Restriction(
                                RestrictionType.DRIVING_RESTRICTION,
                                LocalDateTime.now(),
                                LocalDateTime.now().plusDays(1),
                                "court"
                        )
                )
        );
    }

    @Test
    void changeStatusRespectsRevocationRule() {
        seedIdentity();
        service.changeStatus(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                Status.REVOKED,
                LocalDateTime.now(),
                "fraud"
        );

        assertThrows(DomainValidationException.class, () ->
                service.changeStatus(
                        OrganizationType.CENTRAL_AUTHORITY,
                        id,
                        Status.ACTIVE,
                        LocalDateTime.now().plusDays(1),
                        "retry"
                )
        );
    }

    @Test
    void updateContactPersistsChanges() {
        seedIdentity();
        service.updateContact(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                "Updated",
                "New Address",
                "0700000000",
                "new@example.com"
        );

        String updatedAddress = repository.findById(id).get().getAddress();
        assertEquals("New Address", updatedAddress);
    }

    private void seedIdentity() {
        service.createIdentity(
                OrganizationType.CENTRAL_AUTHORITY,
                id,
                "NAT-001",
                LocalDate.of(1990, 1, 1),
                "London",
                "A. Citizen",
                "10 Sample Street",
                "07123456789",
                "citizen@example.com",
                ResidencyStatus.VALID,
                LocalDateTime.now(),
                "create"
        );
    }
}
