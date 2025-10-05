
# Implementation Plan: Customer Master Data REST API

**Branch**: `001-create-a-new` | **Date**: 2025-10-05 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-create-a-new/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path
   → If not found: ERROR "No feature spec at {path}"
2. Fill Technical Context (scan for NEEDS CLARIFICATION)
   → Detect Project Type from file system structure or context (web=frontend+backend, mobile=app+api)
   → Set Structure Decision based on project type
3. Fill the Constitution Check section based on the content of the constitution document.
4. Evaluate Constitution Check section below
   → If violations exist: Document in Complexity Tracking
   → If no justification possible: ERROR "Simplify approach first"
   → Update Progress Tracking: Initial Constitution Check
5. Execute Phase 0 → research.md
   → If NEEDS CLARIFICATION remain: ERROR "Resolve unknowns"
6. Execute Phase 1 → contracts, data-model.md, quickstart.md, agent-specific template file (e.g., `CLAUDE.md` for Claude Code, `.github/copilot-instructions.md` for GitHub Copilot, `GEMINI.md` for Gemini CLI, `QWEN.md` for Qwen Code, or `AGENTS.md` for all other agents).
7. Re-evaluate Constitution Check section
   → If new violations: Refactor design, return to Phase 1
   → Update Progress Tracking: Post-Design Constitution Check
8. Plan Phase 2 → Describe task generation approach (DO NOT create tasks.md)
9. STOP - Ready for /tasks command
```

**IMPORTANT**: The /plan command STOPS at step 8. Phases 2-4 are executed by other commands:
- Phase 2: /tasks command creates tasks.md
- Phase 3-4: Implementation execution (manual or via tools)

## Summary
Build a REST API for managing customer master data with full CRUD operations (Create, Read, Update, Delete). External systems can synchronize customer information including company name, contact details, email, and phone number. API supports search by multiple fields with partial matching. Uses Java Spring Boot with PostgreSQL database, containerized with Docker, and deployed on Kubernetes via Helm charts.

## Technical Context
**Language/Version**: Java 21+
**Primary Dependencies**: Spring Boot 3.x, Spring Data JPA, Spring Web, PostgreSQL Driver, Maven
**Storage**: PostgreSQL (relational database)
**Testing**: JUnit 5, Spring Boot Test, Testcontainers (for integration tests)
**Target Platform**: Linux container (Docker) deployed on Kubernetes
**Project Type**: single (backend REST API only)
**Performance Goals**: Standard REST API performance (<500ms p95 for reads, <1s for writes)
**Constraints**: Stateless service design for horizontal scaling, no authentication (open API)
**Scale/Scope**: Medium volume (thousands of customers, hundreds of concurrent requests)
**Deployment**: Dockerfile for containerization, Kubernetes manifests, Helm chart for deployment
**Documentation**: README.md with curl command examples for testing all CRUD operations

## Constitution Check
*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The constitution template is not yet populated. Applying standard best practices:
- ✅ **Test-First Development**: All contract tests and integration tests written before implementation
- ✅ **Simplicity**: Single-purpose REST API, no unnecessary abstractions
- ✅ **Observability**: Structured logging with Spring Boot logging framework
- ✅ **Clear Contracts**: OpenAPI specification for all endpoints
- ✅ **Independent Testing**: Testcontainers for isolated database tests

No constitutional violations identified. Proceeding with standard Spring Boot best practices.

## Project Structure

### Documentation (this feature)
```
specs/001-create-a-new/
├── plan.md              # This file (/plan command output)
├── research.md          # Phase 0 output (/plan command)
├── data-model.md        # Phase 1 output (/plan command)
├── quickstart.md        # Phase 1 output (/plan command)
├── contracts/           # Phase 1 output (/plan command)
│   └── openapi.yaml     # OpenAPI 3.0 specification
└── tasks.md             # Phase 2 output (/tasks command - NOT created by /plan)
```

### Source Code (repository root)
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── customer/
│   │           ├── CustomerApplication.java
│   │           ├── model/
│   │           │   └── Customer.java
│   │           ├── repository/
│   │           │   └── CustomerRepository.java
│   │           ├── service/
│   │           │   └── CustomerService.java
│   │           ├── controller/
│   │           │   └── CustomerController.java
│   │           └── dto/
│   │               ├── CustomerRequest.java
│   │               └── CustomerResponse.java
│   └── resources/
│       ├── application.properties
│       └── application-test.properties
└── test/
    └── java/
        └── com/
            └── customer/
                ├── contract/
                │   └── CustomerControllerContractTest.java
                ├── integration/
                │   └── CustomerIntegrationTest.java
                └── unit/
                    └── CustomerServiceTest.java

deployment/
├── Dockerfile
├── kubernetes/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── configmap.yaml
└── helm/
    └── fantaco-customer-main/
        ├── Chart.yaml
        ├── values.yaml
        └── templates/

pom.xml
README.md
```

**Structure Decision**: Selected single project structure (Option 1) as this is a backend-only REST API service. All Java source code follows standard Maven project layout with `src/main/java` for application code and `src/test/java` for tests. Deployment artifacts (Docker, Kubernetes, Helm) are in a separate `deployment/` directory at the repository root.

## Phase 0: Outline & Research

The technical context is well-defined with Java Spring Boot stack specified. Remaining NEEDS CLARIFICATION items (FR-018, FR-020, FR-021, FR-022, FR-023) are deferred as low-impact for MVP. Research will focus on:

1. **Spring Data JPA best practices** for partial text search (case-insensitive LIKE queries)
2. **Testcontainers setup** for PostgreSQL integration tests
3. **OpenAPI documentation** generation with Spring Boot
4. **Kubernetes health checks** and readiness probes for Spring Boot
5. **Helm chart structure** for parameterized deployments

Generating research.md...

## Phase 1: Design & Contracts
*Prerequisites: research.md complete*

Will generate:
1. **data-model.md**: Customer entity with fields, JPA annotations, validation rules
2. **contracts/openapi.yaml**: Complete OpenAPI 3.0 specification with all CRUD endpoints
3. **Contract tests**: Test files verifying request/response schemas (must fail initially)
4. **quickstart.md**: Manual test scenarios using curl commands from README.md
5. **CLAUDE.md update**: Add Java/Spring/Maven/PostgreSQL context

## Phase 2: Task Planning Approach
*This section describes what the /tasks command will do - DO NOT execute during /plan*

**Task Generation Strategy**:
- Load `.specify/templates/tasks-template.md` as base
- Setup phase: Maven project, dependencies, Dockerfile, Kubernetes manifests
- Test phase: Contract tests for each endpoint (POST, GET by ID, GET by fields, PUT, DELETE)
- Core phase: Entity, Repository, Service, Controller, DTOs
- Integration phase: Database configuration, error handling, validation
- Polish phase: README.md with curl examples, Helm chart, logging

**Ordering Strategy**:
- TDD order: Contract tests → Integration tests → Implementation
- Dependency order: Entity → Repository → Service → Controller
- Mark [P] for parallel test creation (different endpoints)

**Estimated Output**: 20-25 numbered tasks in tasks.md

**IMPORTANT**: This phase is executed by the /tasks command, NOT by /plan

## Phase 3+: Future Implementation
*These phases are beyond the scope of the /plan command*

**Phase 3**: Task execution (/tasks command creates tasks.md)
**Phase 4**: Implementation (execute tasks.md following TDD principles)
**Phase 5**: Validation (run all tests, verify curl commands in README.md, deploy to test Kubernetes cluster)

## Complexity Tracking
*Fill ONLY if Constitution Check has violations that must be justified*

No constitutional violations. Using standard Spring Boot layered architecture (Controller → Service → Repository → Entity) which is well-established and appropriate for this use case.

## Progress Tracking
*This checklist is updated during execution flow*

**Phase Status**:
- [X] Phase 0: Research complete (/plan command)
- [X] Phase 1: Design complete (/plan command)
- [X] Phase 2: Task planning complete (/plan command - describe approach only)
- [ ] Phase 3: Tasks generated (/tasks command)
- [ ] Phase 4: Implementation complete
- [ ] Phase 5: Validation passed

**Gate Status**:
- [X] Initial Constitution Check: PASS
- [X] Post-Design Constitution Check: PASS
- [X] All NEEDS CLARIFICATION resolved (deferred low-impact items)
- [X] Complexity deviations documented (none)

---
*Based on Constitution template - See `.specify/memory/constitution.md`*
