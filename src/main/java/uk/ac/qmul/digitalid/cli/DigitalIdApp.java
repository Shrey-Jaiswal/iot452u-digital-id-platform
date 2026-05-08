package uk.ac.qmul.digitalid.cli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import uk.ac.qmul.digitalid.application.AuditActions;
import uk.ac.qmul.digitalid.application.AuthorizationService;
import uk.ac.qmul.digitalid.application.DomainValidationException;
import uk.ac.qmul.digitalid.application.IdentityAuditLog;
import uk.ac.qmul.digitalid.application.IdentityManagementService;
import uk.ac.qmul.digitalid.application.StatusTransitionPolicy;
import uk.ac.qmul.digitalid.application.VerificationService;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.ResidencyStatus;
import uk.ac.qmul.digitalid.domain.Restriction;
import uk.ac.qmul.digitalid.domain.RestrictionType;
import uk.ac.qmul.digitalid.domain.Status;
import uk.ac.qmul.digitalid.persistence.InMemoryDigitalIdRepository;
import uk.ac.qmul.digitalid.rules.BankEmployerRule;
import uk.ac.qmul.digitalid.rules.DrivingLicenceRule;
import uk.ac.qmul.digitalid.rules.ImmigrationRule;
import uk.ac.qmul.digitalid.rules.TaxAuthorityRule;
import uk.ac.qmul.digitalid.rules.VerificationResult;
import uk.ac.qmul.digitalid.rules.VerificationRule;

public final class DigitalIdApp {
    private DigitalIdApp() {
    // Prevent instantiation.
    }

    public static void main(String[] args) {
    System.out.println("Digital ID Platform - demo");

    LocalDateTime t0 = LocalDateTime.of(2026, 5, 1, 9, 0);
    LocalDateTime t1 = t0.plusDays(2);
    LocalDateTime t2 = t1.plusDays(2);
    LocalDateTime t3 = t2.plusDays(1);

    InMemoryDigitalIdRepository repository = new InMemoryDigitalIdRepository();
    AuthorizationService authorizationService = new AuthorizationService();
    IdentityAuditLog auditLog = new IdentityAuditLog();
    StatusTransitionPolicy transitionPolicy = new StatusTransitionPolicy();

    IdentityManagementService managementService = new IdentityManagementService(
        repository,
        authorizationService,
        transitionPolicy,
        auditLog
    );

    UUID id = UUID.randomUUID();
    String nationalId = "NAT-001";

    managementService.createIdentity(
        OrganizationType.CENTRAL_AUTHORITY,
        id,
        nationalId,
        LocalDate.of(1990, 1, 1),
        "London",
        "A. Citizen",
        "10 Sample Street",
        "07123456789",
        "citizen@example.com",
        ResidencyStatus.VALID,
        t0,
        AuditActions.CREATE
    );

    managementService.updateContact(
        OrganizationType.CENTRAL_AUTHORITY,
        id,
        "A. Citizen",
        "20 Updated Street",
        "07123456789",
        "citizen@example.com"
    );

    managementService.addRestriction(
        OrganizationType.CENTRAL_AUTHORITY,
        id,
        new Restriction(
            RestrictionType.DRIVING_RESTRICTION,
            t0.plusDays(1),
            t0.plusDays(5),
            "medical review"
        )
    );

    managementService.changeStatus(
        OrganizationType.CENTRAL_AUTHORITY,
        id,
        Status.SUSPENDED,
        t1,
        "compliance hold"
    );

    managementService.changeStatus(
        OrganizationType.CENTRAL_AUTHORITY,
        id,
        Status.ACTIVE,
        t2,
        "cleared"
    );

    List<VerificationRule> rules = List.of(
        new BankEmployerRule(),
        new DrivingLicenceRule(),
        new ImmigrationRule(),
        new TaxAuthorityRule(t0, t2)
    );

    VerificationService verificationService = new VerificationService(
        repository,
        rules,
        auditLog
    );

    printVerification(verificationService, OrganizationType.BANK_EMPLOYER, nationalId, t0.plusHours(1));
    printVerification(verificationService, OrganizationType.DRIVING_LICENCE_AUTHORITY, nationalId, t0.plusDays(1).plusHours(2));

    managementService.updateResidencyStatus(
        OrganizationType.CENTRAL_AUTHORITY,
        id,
        ResidencyStatus.EXPIRED
    );
    printVerification(verificationService, OrganizationType.IMMIGRATION_AUTHORITY, nationalId, t0.plusDays(3).plusHours(1));

    printVerification(verificationService, OrganizationType.TAX_AUTHORITY, nationalId, t2.plusHours(1));

    managementService.changeStatus(
        OrganizationType.CENTRAL_AUTHORITY,
        id,
        Status.REVOKED,
        t3,
        "fraud"
    );

    try {
        managementService.updateContact(
            OrganizationType.CENTRAL_AUTHORITY,
            id,
            "A. Citizen",
            "30 Invalid Street",
            "07123456789",
            "citizen@example.com"
        );
    } catch (DomainValidationException ex) {
        System.out.println("Expected failure: " + ex.getMessage());
    }

    System.out.println("Audit events recorded: " + auditLog.list().size());
        auditLog.list().stream().limit(3).forEach(event ->
                System.out.println("Audit: " + event.getAction() + " by " + event.getActor())
        );
    }

    private static void printVerification(
        VerificationService verificationService,
        OrganizationType actor,
        String nationalId,
        LocalDateTime requestedAt
    ) {
    VerificationResult result = verificationService.verify(actor, nationalId, requestedAt);
    String verdict = result.isAllowed() ? "ALLOWED" : "DENIED";
    System.out.println(actor + " verification at " + requestedAt + ": " + verdict + " (" + result.getReason() + ")");
    }
}
