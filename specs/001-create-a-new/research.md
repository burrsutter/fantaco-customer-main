# Research: Customer Master Data REST API

**Feature**: Customer Master Data REST API
**Date**: 2025-10-05
**Purpose**: Technical decisions and best practices for Java Spring Boot implementation

## 1. Spring Data JPA Partial Text Search

**Decision**: Use Spring Data JPA derived query methods with `IgnoreCase` and `Containing` keywords

**Rationale**:
- Spring Data JPA automatically generates SQL `LIKE '%value%'` queries with `LOWER()` for case-insensitive matching
- No custom `@Query` annotations needed for simple partial matches
- Leverages Spring Data's query derivation mechanism
- Example: `List<Customer> findByCompanyNameContainingIgnoreCase(String companyName)`

**Alternatives Considered**:
- **Custom @Query with JPQL**: More verbose, requires manual query writing
- **Criteria API**: Overly complex for simple string matching
- **Native SQL queries**: Loses database portability

**Implementation Pattern**:
```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByCompanyNameContainingIgnoreCase(String companyName);
    List<Customer> findByContactNameContainingIgnoreCase(String contactName);
    List<Customer> findByContactEmailContainingIgnoreCase(String contactEmail);
    List<Customer> findByPhoneNumberContaining(String phoneNumber);
}
```

## 2. Testcontainers for PostgreSQL Integration Tests

**Decision**: Use Testcontainers with PostgreSQL Docker image for integration testing

**Rationale**:
- Provides real PostgreSQL database in ephemeral Docker container
- Tests run against actual database, not mocks or H2 in-memory DB
- Ensures queries work correctly with PostgreSQL-specific SQL
- Automatic container lifecycle management (start/stop with test execution)
- CI/CD friendly (works in pipelines with Docker support)

**Alternatives Considered**:
- **H2 in-memory database**: Dialect differences from PostgreSQL cause test/prod inconsistencies
- **Shared test database**: Brittle, requires cleanup, parallel test conflicts
- **DbUnit with fixtures**: Still needs a database instance

**Dependencies Required**:
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

**Implementation Pattern**:
```java
@Testcontainers
@SpringBootTest
class CustomerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

## 3. OpenAPI Documentation with Spring Boot

**Decision**: Use Springdoc OpenAPI library for automatic OpenAPI 3.0 generation

**Rationale**:
- Automatic OpenAPI spec generation from Spring MVC annotations
- Live Swagger UI at `/swagger-ui.html`
- OpenAPI JSON/YAML available at `/v3/api-docs`
- Minimal configuration, annotation-driven
- Active maintenance (better than deprecated Springfox)

**Alternatives Considered**:
- **Manual OpenAPI YAML**: Requires manual sync with code, error-prone
- **Springfox**: Deprecated, no Spring Boot 3 support
- **Custom documentation**: High maintenance overhead

**Dependencies Required**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

**Configuration**:
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

**Annotation Pattern**:
```java
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer", description = "Customer master data management API")
public class CustomerController {

    @PostMapping
    @Operation(summary = "Create a new customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        // ...
    }
}
```

## 4. Kubernetes Health Checks for Spring Boot

**Decision**: Use Spring Boot Actuator for liveness and readiness probes

**Rationale**:
- Built-in `/actuator/health/liveness` and `/actuator/health/readiness` endpoints
- Kubernetes-native probe format
- Automatic detection of application health (database connectivity, etc.)
- Minimal configuration required

**Alternatives Considered**:
- **Custom health endpoints**: Reinvents Spring Boot Actuator functionality
- **TCP socket probes**: Less informative, doesn't check application health

**Dependencies Required**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Configuration**:
```properties
management.endpoint.health.probes.enabled=true
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
management.endpoints.web.exposure.include=health,info
```

**Kubernetes Deployment Pattern**:
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

## 5. Helm Chart Structure

**Decision**: Use standard Helm chart structure with values-based configuration

**Rationale**:
- Industry standard for Kubernetes application packaging
- Environment-specific configuration via values files (`values-dev.yaml`, `values-prod.yaml`)
- Template reusability across deployments
- Built-in versioning and rollback support
- Compatible with GitOps workflows (ArgoCD, Flux)

**Alternatives Considered**:
- **Raw Kubernetes manifests**: No parameterization, duplication across environments
- **Kustomize**: Limited templating, less flexible than Helm
- **Custom shell scripts**: Fragile, non-standard

**Chart Structure**:
```
helm/customer-api/
├── Chart.yaml              # Chart metadata
├── values.yaml             # Default configuration values
├── values-dev.yaml         # Dev environment overrides
├── values-prod.yaml        # Prod environment overrides
└── templates/
    ├── deployment.yaml     # Deployment template
    ├── service.yaml        # Service template
    ├── configmap.yaml      # ConfigMap template
    ├── secret.yaml         # Secret template (database credentials)
    ├── ingress.yaml        # Ingress template (optional)
    └── _helpers.tpl        # Template helpers
```

**Key Values Pattern**:
```yaml
# values.yaml
replicaCount: 2

image:
  repository: customer-api
  tag: "1.0.0"
  pullPolicy: IfNotPresent

service:
  type: ClusterIP
  port: 80
  targetPort: 8080

database:
  host: postgres-service
  port: 5432
  name: customerdb

resources:
  limits:
    cpu: 500m
    memory: 512Mi
  requests:
    cpu: 250m
    memory: 256Mi
```

## 6. Maven Project Configuration

**Decision**: Use Spring Boot 3.2.x with Maven for dependency management and build

**Rationale**:
- Spring Boot 3.x requires Java 17+, supports Java 21 LTS
- Maven is widely supported, familiar to Java developers
- Spring Boot Maven plugin handles packaging, containerization
- Dependency management via Spring Boot BOM (Bill of Materials)

**Key Dependencies**:
- `spring-boot-starter-web`: REST API support
- `spring-boot-starter-data-jpa`: Database access
- `spring-boot-starter-validation`: Bean validation
- `spring-boot-starter-actuator`: Health checks
- `postgresql`: PostgreSQL driver
- `springdoc-openapi-starter-webmvc-ui`: OpenAPI documentation
- `spring-boot-starter-test`: Testing framework
- `testcontainers`: Integration testing

**Build Configuration**:
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

## 7. Docker Image Best Practices

**Decision**: Use multi-stage Dockerfile with Red Hat UBI9 OpenJDK 21 runtime image

**Rationale**:
- Multi-stage build: smaller final image (build artifacts not included)
- UBI9 (Universal Base Image): Red Hat supported, enterprise-grade, frequent security updates
- OpenJDK 21: Latest LTS version with modern features
- Non-root user for security (UBI images run as non-root by default)
- Layer caching optimization (dependencies cached separately from code)
- Compatible with OpenShift and enterprise Kubernetes environments

**Dockerfile Pattern**:
```dockerfile
# Build stage
FROM registry.access.redhat.com/ubi9/openjdk-21:latest AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 8. Error Handling and Validation

**Decision**: Use Spring Boot's `@ControllerAdvice` for global exception handling and Bean Validation for input validation

**Rationale**:
- Centralized error handling across all controllers
- Consistent error response format
- Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`) declarative and clean
- Spring automatically returns HTTP 400 for validation failures

**Pattern**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Customer not found", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // ... format validation errors
    }
}
```

## Summary

All technical decisions support the requirements:
- ✅ CRUD operations via Spring Data JPA
- ✅ Partial text search with case-insensitive matching
- ✅ PostgreSQL database with Testcontainers for testing
- ✅ OpenAPI documentation auto-generated
- ✅ Docker containerization with multi-stage build
- ✅ Kubernetes deployment with Helm charts
- ✅ Health checks via Spring Boot Actuator
- ✅ README.md will contain curl examples for manual testing

No blockers identified. Ready to proceed to Phase 1 (Design & Contracts).
