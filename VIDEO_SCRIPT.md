# Video Demo Script (IOT452U)

Target length: 6-10 minutes

## 1. Intro (30s)
- Module + coursework name
- Problem summary: Digital ID lifecycle and multi-organisation verification
- Tech stack: Java 17 + Maven + JUnit + GitHub Actions

## 2. Architecture overview (90s)
- Domain: DigitalId, Status, StatusHistoryEntry, Restriction, ResidencyStatus
- Application services: IdentityManagementService, VerificationService, AuthorizationService
- Rules: per-organisation verification rules
- Persistence: InMemoryDigitalIdRepository
- Audit log: IdentityAuditLog

## 3. Core lifecycle demo (2-3 min)
- Create Digital ID (Central Authority)
- Update contact data
- Add restriction + change status (suspension then re-activation)
- Revoke and attempt invalid update (show deterministic rejection)

## 4. Verification scenarios (2-3 min)
- Bank/Employer: validity-only
- Driving authority: blocked by driving restriction
- Immigration: blocked by invalid residency/hold
- Tax: blocked by suspension during reporting period

## 5. Tests and CI (60-90s)
- Show `mvn test`
- Show GitHub Actions CI workflow
- Mention key edge cases covered

## 6. Wrap-up (30s)
- Summary of design choices
- Emphasize deterministic rules, auditability, and clean structure
