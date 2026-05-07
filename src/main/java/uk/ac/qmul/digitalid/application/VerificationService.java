package uk.ac.qmul.digitalid.application;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import uk.ac.qmul.digitalid.domain.DigitalId;
import uk.ac.qmul.digitalid.domain.OrganizationType;
import uk.ac.qmul.digitalid.persistence.DigitalIdRepository;
import uk.ac.qmul.digitalid.rules.VerificationResult;
import uk.ac.qmul.digitalid.rules.VerificationRule;

public final class VerificationService {
    private final DigitalIdRepository repository;
    private final Map<OrganizationType, VerificationRule> rules;
    private final IdentityAuditLog auditLog;

    public VerificationService(
            DigitalIdRepository repository,
            List<VerificationRule> rules,
            IdentityAuditLog auditLog
    ) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
        this.rules = new HashMap<>();
        for (VerificationRule rule : rules) {
            this.rules.put(rule.supportedActor(), rule);
        }
    }

    public VerificationResult verify(OrganizationType actor, String nationalId, LocalDateTime requestedAt) {
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(nationalId, "nationalId");
        Objects.requireNonNull(requestedAt, "requestedAt");

        DigitalId digitalId = repository.findByNationalId(nationalId)
                .orElseThrow(() -> new DomainValidationException("Digital ID not found"));

        VerificationRule rule = rules.get(actor);
        if (rule == null) {
            throw new DomainValidationException("No verification rule for actor: " + actor);
        }

        VerificationResult result = rule.verify(digitalId, requestedAt);
        auditLog.record(new IdentityAuditEvent(
                digitalId.getId(),
                actor,
                "VERIFY:" + actor,
                requestedAt
        ));

        return result;
    }
}
