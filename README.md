# Digital ID Platform (IOT452U Individual Coursework)

Console-based backend system for managing Digital IDs and verifying identities for multiple organisations.

Repository: https://github.qmul.ac.uk/ec25780/individual-coursework.git

## Overview
The system models a Digital ID lifecycle managed by a central authority. Other organisations verify identities with
organisation-specific rules. Operations are deterministic, validated, and recorded for auditability.

## Requirements
- Java 17
- Maven 3.8+

## Run the demo
```bash
mvn test
mvn -q -DskipTests package
java -cp target/classes uk.ac.qmul.digitalid.cli.DigitalIdApp
```

## Project structure
- `uk.ac.qmul.digitalid.domain`: core domain model (DigitalId, status, history, restrictions)
- `uk.ac.qmul.digitalid.application`: services, policies, and audit logging
- `uk.ac.qmul.digitalid.rules`: verification rules per organisation
- `uk.ac.qmul.digitalid.persistence`: repository interfaces and in-memory store
- `uk.ac.qmul.digitalid.cli`: console demo runner

## Verification rules
- Bank/Employer: validity-only (active at request time)
- Driving Licence Authority: active and no driving restriction
- Immigration Authority: active, residency valid, no immigration hold
- Tax Authority: active and not suspended during the reporting period