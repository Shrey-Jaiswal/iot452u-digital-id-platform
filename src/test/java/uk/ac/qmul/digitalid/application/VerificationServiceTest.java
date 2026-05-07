package uk.ac.qmul.digitalid.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.domain.ResidencyStatus;
import uk.ac.qmul.digitalid.persistence.InMemoryDigitalIdRepository;
import uk.ac.qmul.digitalid.rules.BankEmployerRule;
import uk.ac.qmul.digitalid.rules.VerificationResult;

class VerificationServiceTest {
    private InMemoryDigitalIdRepository repository;
    private IdentityAuditLog auditLog;
    private VerificationService service;

    @BeforeEach
    void setup() {
        repository = new InMemoryDigitalIdRepository();
        auditLog = new IdentityAuditLog();
        service = new VerificationService(repository, List.of(new BankEmployerRule()), auditLog);
    }

    @Test
    void verifiesUsingConfiguredRule() {
        DigitalId digitalId = seedIdentity("NAT-100");
        VerificationResult result = service.verify(
                OrganizationType.BANK_EMPLOYER,
                digitalId.getNationalId(),
                LocalDateTime.now()
        );

        assertTrue(result.isAllowed());
        assertEquals(1, auditLog.list().size());
    }

    @Test
    void rejectsUnknownNationalId() {
        assertThrows(DomainValidationException.class, () ->
                service.verify(OrganizationType.BANK_EMPLOYER, "UNKNOWN", LocalDateTime.now())
        );
    }

    @Test
    void rejectsMissingRule() {
        seedIdentity("NAT-200");
        assertThrows(DomainValidationException.class, () ->
                service.verify(OrganizationType.TAX_AUTHORITY, "NAT-200", LocalDateTime.now())
        );
    }

    private DigitalId seedIdentity(String nationalId) {
        DigitalId digitalId = DigitalId.createActive(
                UUID.randomUUID(),
                nationalId,
                LocalDate.of(1990, 1, 1),
                "London",
                "Test Person",
                "1 Sample Road",
                "0123456789",
                "test@example.com",
                ResidencyStatus.VALID,
                LocalDateTime.now(),
                "create"
        );
        repository.save(digitalId);
        return digitalId;
    }
}
