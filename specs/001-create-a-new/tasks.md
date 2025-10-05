# Tasks: Customer Master Data REST API

**Input**: Design documents from `/specs/001-create-a-new/`
**Prerequisites**: plan.md, research.md, data-model.md, contracts/openapi.yaml, quickstart.md

## Execution Flow (main)
```
1. Load plan.md from feature directory
   → Extract: Java 21, Spring Boot 3.x, PostgreSQL, Maven
2. Load design documents:
   → data-model.md: Customer entity with 12 fields
   → contracts/openapi.yaml: 5 REST endpoints
   → research.md: Spring Data JPA, Testcontainers, Springdoc OpenAPI
   → quickstart.md: 7 test scenarios + 8 edge cases
3. Generate tasks by category:
   → Setup: Maven project, dependencies, Docker, Kubernetes
   → Tests: Contract tests (5 endpoints), Integration tests (7 scenarios)
   → Core: Customer entity, repository, service, controller, DTOs
   → Integration: Database config, error handling, logging
   → Polish: README with curl examples, Helm chart
4. Apply task rules:
   → Different test files = [P]
   → Entity creation = [P] from tests
   → Tests before implementation (TDD)
5. Task count: 32 tasks total
```

## Format: `[ID] [P?] Description`
- **[P]**: Can run in parallel (different files, no dependencies)
- Include exact file paths in descriptions

## Path Conventions
This is a single Maven project following standard Spring Boot structure:
- Source: `src/main/java/com/customer/`
- Tests: `src/test/java/com/customer/`
- Resources: `src/main/resources/`
- Deployment: `deployment/`

---

## Phase 3.1: Setup

- [ ] **T001** Create Maven project structure with `pom.xml` including Spring Boot 3.2.0, Spring Data JPA, Spring Web, PostgreSQL driver, Springdoc OpenAPI, Spring Boot Actuator, Testcontainers dependencies

- [ ] **T002** Create Spring Boot main application class `src/main/java/com/customer/CustomerApplication.java` with `@SpringBootApplication` annotation

- [ ] **T003** Create `src/main/resources/application.properties` with PostgreSQL datasource configuration, JPA/Hibernate settings, logging configuration

- [ ] **T004** Create `src/main/resources/application-test.properties` for test profile with H2 or Testcontainers PostgreSQL configuration

- [ ] **T005** Create `deployment/Dockerfile` with multi-stage build using Red Hat UBI9 OpenJDK 21 images (build stage: `ubi9/openjdk-21`, runtime stage: `ubi9/openjdk-21-runtime`) following research.md guidelines

- [ ] **T006** Create `deployment/kubernetes/deployment.yaml` with Kubernetes Deployment manifest including health probes, resource limits, PostgreSQL connection configuration

- [ ] **T007** Create `deployment/kubernetes/service.yaml` with Kubernetes Service manifest exposing port 8080

- [ ] **T008** Create `deployment/kubernetes/configmap.yaml` with ConfigMap for application configuration

---

## Phase 3.2: Tests First (TDD) ⚠️ MUST COMPLETE BEFORE 3.3

**CRITICAL: These tests MUST be written and MUST FAIL before ANY implementation**

### Contract Tests (API Endpoint Validation)

- [ ] **T009** [P] Create contract test `src/test/java/com/customer/contract/CreateCustomerContractTest.java` for POST /api/customers - validate request schema (customerId 5 chars, companyName required, all optional fields), validate 201 response with Location header, validate 400 for invalid input, validate 409 for duplicate customerId

- [ ] **T010** [P] Create contract test `src/test/java/com/customer/contract/GetCustomerByIdContractTest.java` for GET /api/customers/{customerId} - validate 200 response schema with all 14 fields, validate 404 for non-existent customer

- [ ] **T011** [P] Create contract test `src/test/java/com/customer/contract/SearchCustomersContractTest.java` for GET /api/customers with query params - validate companyName/contactName/contactEmail/phone search parameters, validate array response, validate empty array for no results

- [ ] **T012** [P] Create contract test `src/test/java/com/customer/contract/UpdateCustomerContractTest.java` for PUT /api/customers/{customerId} - validate request schema (companyName required, customerId in path not body), validate 200 response, validate 404 for non-existent customer, validate 400 for invalid input

- [ ] **T013** [P] Create contract test `src/test/java/com/customer/contract/DeleteCustomerContractTest.java` for DELETE /api/customers/{customerId} - validate 204 no content response, validate 404 for non-existent customer

### Integration Tests (End-to-End Scenarios from quickstart.md)

- [ ] **T014** [P] Create integration test `src/test/java/com/customer/integration/CreateCustomerIntegrationTest.java` using Testcontainers PostgreSQL - test create with full data, test create with minimal data (only required fields), verify database persistence

- [ ] **T015** [P] Create integration test `src/test/java/com/customer/integration/RetrieveCustomerIntegrationTest.java` using Testcontainers - test get by customerId, verify all fields returned correctly

- [ ] **T016** [P] Create integration test `src/test/java/com/customer/integration/SearchCustomerIntegrationTest.java` using Testcontainers - test partial match search by companyName, contactName, contactEmail, phone; test case-insensitive search; test no results scenario

- [ ] **T017** [P] Create integration test `src/test/java/com/customer/integration/UpdateCustomerIntegrationTest.java` using Testcontainers - test update existing customer, verify updatedAt timestamp changes, test 404 for non-existent customer

- [ ] **T018** [P] Create integration test `src/test/java/com/customer/integration/DeleteCustomerIntegrationTest.java` using Testcontainers - test hard delete, verify subsequent GET returns 404, test delete non-existent customer returns 404

- [ ] **T019** [P] Create integration test `src/test/java/com/customer/integration/ValidationIntegrationTest.java` using Testcontainers - test customerId length validation (must be 5 chars), test companyName required, test field max length validations, test email format validation

- [ ] **T020** [P] Create integration test `src/test/java/com/customer/integration/DuplicateCustomerIdTest.java` using Testcontainers - test creating customer with duplicate customerId returns 409 Conflict

---

## Phase 3.3: Core Implementation (ONLY after tests are failing)

### Data Model & DTOs

- [ ] **T021** Create JPA entity `src/main/java/com/customer/model/Customer.java` with 14 fields (customerId as @Id String 5 chars, companyName VARCHAR(40) NOT NULL, 10 optional fields, createdAt/updatedAt timestamps), Bean Validation annotations (@NotBlank, @Size, @Email), indexes for search fields (see data-model.md)

- [ ] **T022** [P] Create DTO `src/main/java/com/customer/dto/CustomerRequest.java` as Java record with customerId (required, 5 chars), companyName (required, max 40), 10 optional fields with validation annotations matching data-model.md

- [ ] **T023** [P] Create DTO `src/main/java/com/customer/dto/CustomerUpdateRequest.java` as Java record with companyName (required), 10 optional fields (customerId excluded - comes from path parameter)

- [ ] **T024** [P] Create DTO `src/main/java/com/customer/dto/CustomerResponse.java` as Java record with all 14 fields including createdAt and updatedAt

### Repository Layer

- [ ] **T025** Create Spring Data JPA repository `src/main/java/com/customer/repository/CustomerRepository.java` extending JpaRepository<Customer, String> with query methods: `findByCompanyNameContainingIgnoreCase`, `findByContactNameContainingIgnoreCase`, `findByContactEmailContainingIgnoreCase`, `findByPhoneContaining`

### Service Layer

- [ ] **T026** Create service `src/main/java/com/customer/service/CustomerService.java` with methods: `createCustomer(CustomerRequest)` returning CustomerResponse (validate uniqueness, throw exception for duplicate customerId), `getCustomerById(String customerId)` returning CustomerResponse (throw exception if not found), `searchCustomers(String companyName, String contactName, String contactEmail, String phone)` returning List<CustomerResponse> (support partial text search, case-insensitive), `updateCustomer(String customerId, CustomerUpdateRequest)` returning CustomerResponse (throw exception if not found), `deleteCustomer(String customerId)` (hard delete, throw exception if not found)

### Controller Layer

- [ ] **T027** Create REST controller `src/main/java/com/customer/controller/CustomerController.java` with 5 endpoints matching OpenAPI spec: POST /api/customers (returns 201 with Location header, 400 for validation errors, 409 for duplicate ID), GET /api/customers/{customerId} (returns 200 or 404), GET /api/customers with query params (returns 200 with array), PUT /api/customers/{customerId} (returns 200, 404, or 400), DELETE /api/customers/{customerId} (returns 204 or 404). Include @Tag and @Operation annotations for OpenAPI docs

---

## Phase 3.4: Integration

### Error Handling & Validation

- [ ] **T028** Create global exception handler `src/main/java/com/customer/exception/GlobalExceptionHandler.java` using @RestControllerAdvice - handle EntityNotFoundException (404), MethodArgumentNotValidException (400 with field-level errors), DataIntegrityViolationException for duplicate IDs (409), generic exceptions (500). Return ErrorResponse DTO with timestamp, status, error, message, and errors array

- [ ] **T029** [P] Create exception classes: `src/main/java/com/customer/exception/CustomerNotFoundException.java`, `src/main/java/com/customer/exception/DuplicateCustomerIdException.java`

- [ ] **T030** [P] Create `src/main/java/com/customer/dto/ErrorResponse.java` as Java record matching OpenAPI ErrorResponse schema (timestamp, status, error, message, errors list)

### Configuration & Documentation

- [ ] **T031** Update `src/main/resources/application.properties` with Springdoc OpenAPI configuration (api-docs path, swagger-ui path), Spring Boot Actuator health endpoints enabled, logging levels for com.customer package

---

## Phase 3.5: Polish

### Documentation

- [ ] **T032** Create `README.md` at repository root with: project description, prerequisites (Java 17, Maven, PostgreSQL/Docker), build instructions (`mvn clean package`), run instructions (`mvn spring-boot:run`), Docker build/run commands, curl examples for all 5 endpoints (GET, POST, PUT, DELETE) from quickstart.md, OpenAPI/Swagger UI URL (http://localhost:8080/swagger-ui.html), health check URL (http://localhost:8080/actuator/health)

### Deployment

- [ ] **T033** Create Helm chart structure `deployment/helm/fantaco-customer-main/Chart.yaml` with chart metadata (name, version, description, appVersion)

- [ ] **T034** Create `deployment/helm/fantaco-customer-main/values.yaml` with default values: replicaCount, image (repository, tag, pullPolicy), service (type, port, targetPort), database (host, port, name), resources (limits, requests), ingress configuration

- [ ] **T035** [P] Create Helm templates: `deployment/helm/fantaco-customer-main/templates/deployment.yaml`, `deployment/helm/fantaco-customer-main/templates/service.yaml`, `deployment/helm/fantaco-customer-main/templates/configmap.yaml`, `deployment/helm/fantaco-customer-main/templates/_helpers.tpl`

---

## Dependencies

**Critical Path**:
1. Setup (T001-T008) → Tests (T009-T020) → Core (T021-T027) → Integration (T028-T031) → Polish (T032-T035)

**Detailed Dependencies**:
- T001-T008 (Setup) must complete before any other tasks
- T009-T020 (All tests) must complete and FAIL before T021 (entity creation)
- T021 (Customer entity) blocks T025 (repository - needs entity)
- T022-T024 (DTOs) block T026 (service - needs DTOs)
- T025 (repository) + T026 (service dependencies) block T026 completion
- T026 (service) blocks T027 (controller - needs service)
- T027 (controller) blocks T028 (exception handler - needs controller endpoints)
- T028-T031 (error handling) should complete before final testing
- T032-T035 (documentation & deployment) can start after T027

**Parallel Execution Groups**:
- Group 1: T002, T003, T004 (different config files)
- Group 2: T005, T006, T007, T008 (different deployment files)
- Group 3: T009-T020 (all test files - different files, no dependencies)
- Group 4: T022, T023, T024 (different DTO files)
- Group 5: T029, T030 (different exception/DTO files)
- Group 6: T035 (multiple Helm template files)

---

## Parallel Execution Examples

### Execute All Contract Tests in Parallel (T009-T013)
```bash
# These 5 tests can run simultaneously - different files, no shared state
mvn test -Dtest=CreateCustomerContractTest &
mvn test -Dtest=GetCustomerByIdContractTest &
mvn test -Dtest=SearchCustomersContractTest &
mvn test -Dtest=UpdateCustomerContractTest &
mvn test -Dtest=DeleteCustomerContractTest &
wait
```

### Execute All Integration Tests in Parallel (T014-T020)
```bash
# These 7 tests use Testcontainers with isolated databases
mvn test -Dtest=CreateCustomerIntegrationTest &
mvn test -Dtest=RetrieveCustomerIntegrationTest &
mvn test -Dtest=SearchCustomerIntegrationTest &
mvn test -Dtest=UpdateCustomerIntegrationTest &
mvn test -Dtest=DeleteCustomerIntegrationTest &
mvn test -Dtest=ValidationIntegrationTest &
mvn test -Dtest=DuplicateCustomerIdTest &
wait
```

### Create All DTOs in Parallel (T022-T024)
```bash
# Different files, no dependencies between them
# Task: Create CustomerRequest DTO
# Task: Create CustomerUpdateRequest DTO
# Task: Create CustomerResponse DTO
```

---

## Notes

- **[P] tasks** = different files, no dependencies - safe for parallel execution
- **TDD enforcement**: All tests (T009-T020) MUST fail before implementing T021-T027
- **Testcontainers**: Integration tests use PostgreSQL containers for isolation
- **Validation**: Bean Validation enforced at controller layer with @Valid
- **Search**: Partial text search via Spring Data JPA `Containing` + `IgnoreCase`
- **Error handling**: Global exception handler provides consistent error responses
- **OpenAPI**: Springdoc auto-generates docs from annotations
- **Customer ID**: 5-character user-provided string (not auto-increment)
- **Hard delete**: No soft delete - records permanently removed
- **No authentication**: Open API per requirements clarification

---

## Validation Checklist

- [X] All 5 API endpoints have contract tests (T009-T013)
- [X] Customer entity has model task (T021)
- [X] All 12 tests come before implementation tasks
- [X] Parallel tasks [P] are in different files
- [X] Each task specifies exact file path
- [X] No [P] task modifies same file as another [P] task
- [X] Dependencies properly ordered (Setup → Tests → Core → Integration → Polish)

**Task Generation**: SUCCESS - 35 tasks ready for execution
