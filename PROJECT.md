# IOT452U Digital ID Platform - Project Plan

## Purpose
Build a console-based backend system in Java that demonstrates correct Digital ID lifecycle management and identity verification for multiple consuming organisations. The system emphasises deterministic behaviour, clear structure, automated tests, and professional development practices to target the top marking band.

## Repository
- GitHub URL: https://github.qmul.ac.uk/ec25780/individual-coursework.git
- Default branch: main
- Build tool: Maven

## Assessment Targets (Top Band)
- Correct behaviour across representative workflows with consistent error handling.
- Clean architecture and professional code quality with justified design choices.
- Automated unit tests and CI that reliably build and run tests.
- Evident incremental development via commits and a task board.
- Clear and structured technical communication (README and video).

## Core Scenario Summary
A central authority manages Digital IDs (create, update, status changes). Other organisations verify identities or perform limited lookups with organisation-specific rules. Operations must be deterministic and enforce business rules and authorisation. Key actions are recorded for audit.

## Scope
### In Scope
- Digital ID creation, update of permitted attributes, and status transitions.
- Immutable vs mutable attributes enforced by the system.
- Status history with effective dates and reasons.
- Restrictions with types and optional periods.
- Organisation-specific verification rules.
- Validation, authorisation, and consistent rejection of invalid operations.
- Console-based interface to demonstrate workflows.
- Unit tests and CI pipeline.

### Out of Scope
- Web UI or external APIs.
- Persistent database (use in-memory repository with clean interface).
- Authentication/identity of portal users (simulated via request metadata).

## Actors and Organisation Rules
- CentralAuthority: only actor allowed to create, update, and change status.
- ImmigrationAuthority: ID must be Active, not under ImmigrationHold restriction, and ResidencyStatus is VALID.
- TaxAuthority: ID must be Active and not Suspended during a given period.
- DrivingLicenceAuthority: ID must be Active and not under DrivingRestriction.
- BankEmployer: validity-only response (Active at time of request), no extra attributes.

## Data Model (Planned)
### DigitalId (Aggregate Root)
Immutable:
- id (UUID)
- nationalId (String)
- dateOfBirth (LocalDate)
- placeOfBirth (String)

Mutable:
- fullName (String)
- address (String)
- phone (String)
- email (String)

Controlled:
- currentStatus (Status)
- statusHistory (List<StatusHistoryEntry>)
- restrictions (List<Restriction>)
- residencyStatus (ResidencyStatus)

### Status
- ACTIVE
- SUSPENDED
- REVOKED

### StatusHistoryEntry
- status (Status)
- effectiveFrom (LocalDateTime)
- effectiveTo (LocalDateTime, nullable)
- reason (String)

### Restriction
- type (RestrictionType)
- effectiveFrom (LocalDateTime)
- effectiveTo (LocalDateTime, nullable)
- reason (String)

### RestrictionType
- DRIVING_RESTRICTION
- IMMIGRATION_HOLD
- OTHER (future extension)

### ResidencyStatus
- VALID
- EXPIRED
- UNKNOWN

## Business Rules (Planned)
- Only CentralAuthority can create or modify Digital IDs.
- Immutable attributes cannot be changed after creation.
- Status transitions are validated; attempts to update a REVOKED ID are rejected.
- Repeated operations are deterministic (idempotent where possible).
- Verification uses organisation-specific rules and returns minimal required data.
- All key actions are recorded in an audit log.
- ResidencyStatus can only be modified by CentralAuthority.

## Architecture
### Package Structure (Planned)
- uk.ac.qmul.digitalid.domain
- uk.ac.qmul.digitalid.application
- uk.ac.qmul.digitalid.rules
- uk.ac.qmul.digitalid.persistence
- uk.ac.qmul.digitalid.cli

### Key Components
- IdentityManagementService: create, update, status changes.
- VerificationService: verify and lookup per organisation.
- AuthorizationService: validates actor permissions.
- VerificationRule interface: per organisation rules.
- DigitalIdRepository: in-memory implementation with interface.
- AuditLog: records all key actions.

## Console Demo Flows (Planned)
1) Create a Digital ID (CentralAuthority).
2) Update mutable fields (CentralAuthority).
3) Suspend then verify (TaxAuthority period check fails during suspension).
4) Add DrivingRestriction and verify (DrivingLicenceAuthority fails).
5) ImmigrationHold and verify (ImmigrationAuthority fails).
6) BankEmployer verification (validity-only response).
7) Attempt invalid update on REVOKED ID (rejected deterministically).

## Testing Strategy
- JUnit 5 unit tests for:
  - Status transitions and history.
  - Immutable attribute enforcement.
  - Period checks for suspension.
  - Restriction checks for Driving and Immigration.
  - Authorisation enforcement.
- One test per organisation rule plus negative cases.

## Continuous Integration
- GitHub Actions workflow to build and run tests on push and pull request.
- Maven commands: mvn -q -e -DskipTests=false test

## Deliverables Checklist
- README with repo URL, run instructions, and architecture overview.
- Video demo (<=10 minutes) showing behaviour, structure, and decisions.
- ZIP of full repo matching video contents.

## Project Board
### Columns
- Backlog
- In Progress
- Review
- Done

### GitHub Issues Import
- Source file: `issues.csv`
- Columns: title, body, labels (semicolon-delimited), status

### Optional Project Sync Script (Local Only)
- File: `scripts/github_project_sync.py` (ignored by git)
- Purpose: create issues from `issues.csv` and add them to a Projects v2 board
- Required env: `GITHUB_TOKEN`

### Issue Tracker (Planned)
ID | Title | Description | Acceptance Criteria
---|---|---|---
I01 | Project skeleton | Maven project structure with packages | Builds with mvn test; base modules present
I02 | Domain model | Implement DigitalId, status, history, restrictions | Unit tests for value rules pass
I03 | Repository | In-memory repo interface and impl | CRUD operations covered by tests
I04 | Authorization | Enforce CentralAuthority-only mutations | Unauthorized operations rejected
I05 | Identity management | Create/update/status change flows | All rule tests pass
I06 | Verification rules | Per-org verification rules | Tests pass for all orgs
I07 | Audit log | Record key actions | Entries created for each operation
I08 | CLI demo | Console runner with sample flows | Runs and prints expected outputs
I09 | CI pipeline | GitHub Actions build and test | Workflow passes on push
I10 | README | Usage + architecture overview | Clear run steps and component summary
I11 | Video script | Demo outline and talking points | Covers rules, structure, and tests
I12 | Unit test suite | Implement JUnit tests for core rules | All core rules covered with positive and negative tests

### Initial Board State
- Backlog: I01, I02, I03, I04, I05, I06, I07, I08, I09, I10, I11, I12
- In Progress:
- Review:
- Done:

## Risks and Mitigations
- Risk: Rules unclear for edge cases. Mitigation: define deterministic handling and add tests.
- Risk: Time constraints. Mitigation: prioritize core rules and essential tests.

## Open Decisions
- Package name (default: com.example.digitalid) -> uk.ac.qmul.digitalid
- Repo URL -> https://github.qmul.ac.uk/ec25780/individual-coursework.git
- Include ResidencyStatus for ImmigrationAuthority -> Yes (VALID required)
