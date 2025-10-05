# Data Model: Customer Master Data REST API

**Feature**: Customer Master Data REST API
**Date**: 2025-10-05
**Updated**: 2025-10-05 (schema revision)
**Purpose**: Entity definitions, relationships, and validation rules

## Entities

### Customer

**Description**: Represents a business customer in the master database. The central entity for storing customer information that external systems can create, read, update, and delete.

**Fields**:

| Field Name     | Type          | Constraints                      | Description                                      |
|----------------|---------------|----------------------------------|--------------------------------------------------|
| customerId     | String        | PRIMARY KEY, LENGTH(5)           | 5-character customer identifier (user-provided)  |
| companyName    | String        | NOT NULL, MAX_LENGTH(40)         | Business/company name                            |
| contactName    | String        | NULLABLE, MAX_LENGTH(30)         | Contact person name                              |
| contactTitle   | String        | NULLABLE, MAX_LENGTH(30)         | Contact person title/position                    |
| address        | String        | NULLABLE, MAX_LENGTH(60)         | Street address                                   |
| city           | String        | NULLABLE, MAX_LENGTH(15)         | City                                             |
| region         | String        | NULLABLE, MAX_LENGTH(15)         | State/Province/Region                            |
| postalCode     | String        | NULLABLE, MAX_LENGTH(10)         | Postal/ZIP code                                  |
| country        | String        | NULLABLE, MAX_LENGTH(15)         | Country                                          |
| phone          | String        | NULLABLE, MAX_LENGTH(24)         | Primary phone number                             |
| fax            | String        | NULLABLE, MAX_LENGTH(24)         | Fax number                                       |
| contactEmail   | String        | NULLABLE, MAX_LENGTH(255)        | Contact email address                            |
| createdAt      | Timestamp     | NOT NULL, DEFAULT CURRENT_TIME   | Record creation timestamp                        |
| updatedAt      | Timestamp     | NOT NULL, DEFAULT CURRENT_TIME   | Last update timestamp (updated on every change)  |

**Indexes**:
- **Primary Index**: `customer_id` (clustered)
- **Search Indexes** (for performance on partial text search):
  - `idx_company_name` on `company_name` (B-tree index for LIKE queries)
  - `idx_contact_name` on `contact_name` (B-tree index for LIKE queries)
  - `idx_contact_email` on `contact_email` (B-tree index for LIKE queries)
  - `idx_phone` on `phone` (B-tree index for LIKE queries)

**Validation Rules** (enforced at application layer):

| Rule ID | Field         | Validation                                      | Error Message                              |
|---------|---------------|-------------------------------------------------|--------------------------------------------|
| V-001   | customerId    | Must not be null or blank                       | "Customer ID is required"                  |
| V-002   | customerId    | Must be exactly 5 characters                    | "Customer ID must be exactly 5 characters" |
| V-003   | customerId    | Must be unique                                  | "Customer ID already exists"               |
| V-004   | companyName   | Must not be null or blank                       | "Company name is required"                 |
| V-005   | companyName   | Max length 40 characters                        | "Company name must not exceed 40 chars"    |
| V-006   | contactName   | Max length 30 characters (if provided)          | "Contact name must not exceed 30 chars"    |
| V-007   | contactTitle  | Max length 30 characters (if provided)          | "Contact title must not exceed 30 chars"   |
| V-008   | address       | Max length 60 characters (if provided)          | "Address must not exceed 60 chars"         |
| V-009   | city          | Max length 15 characters (if provided)          | "City must not exceed 15 chars"            |
| V-010   | region        | Max length 15 characters (if provided)          | "Region must not exceed 15 chars"          |
| V-011   | postalCode    | Max length 10 characters (if provided)          | "Postal code must not exceed 10 chars"     |
| V-012   | country       | Max length 15 characters (if provided)          | "Country must not exceed 15 chars"         |
| V-013   | phone         | Max length 24 characters (if provided)          | "Phone must not exceed 24 chars"           |
| V-014   | fax           | Max length 24 characters (if provided)          | "Fax must not exceed 24 chars"             |
| V-015   | contactEmail  | Max length 255 characters (if provided)         | "Contact email must not exceed 255 chars"  |
| V-016   | contactEmail  | Must be valid email format (if provided)        | "Contact email must be valid"              |

**Note on Validation**:
- **customerId** is now user-provided (not auto-generated) and must be unique
- Only **customerId** and **companyName** are required fields
- All other fields are optional (nullable)
- Email format validation is basic (pattern check for `@` and `.`)

**Uniqueness Constraints**:
- **customerId** must be unique (PRIMARY KEY)
- Multiple customers can have the same **contactEmail**, **companyName**, etc.

**Lifecycle**:
1. **Creation**: External system sends POST request with **customerId** (5 chars) and **companyName** (required) → Customer record created with `createdAt` and `updatedAt` timestamps
2. **Retrieval**: External system sends GET request with `customerId` or search parameters → Customer record(s) returned
3. **Update**: External system sends PUT request with `customerId` → Customer fields updated, `updatedAt` timestamp refreshed
4. **Deletion**: External system sends DELETE request with `customerId` → Customer record permanently removed (hard delete)

**State Transitions**: None (customers have no state machine; records are active until deleted)

**Concurrency Strategy**: Last-write-wins (clarification from FR-019)
- No optimistic locking (no version field)
- No pessimistic locking
- Most recent update overwrites previous changes
- No conflict detection or resolution

## Relationships

**Customer** has NO relationships to other entities. This is a standalone master data table with no foreign keys.

## Database Schema (PostgreSQL DDL)

```sql
CREATE TABLE customer (
    customer_id VARCHAR(5) PRIMARY KEY,
    company_name VARCHAR(40) NOT NULL,
    contact_name VARCHAR(30),
    contact_title VARCHAR(30),
    address VARCHAR(60),
    city VARCHAR(15),
    region VARCHAR(15),
    postal_code VARCHAR(10),
    country VARCHAR(15),
    phone VARCHAR(24),
    fax VARCHAR(24),
    contact_email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for search performance (case-insensitive LIKE queries)
CREATE INDEX idx_company_name ON customer USING btree (LOWER(company_name));
CREATE INDEX idx_contact_name ON customer USING btree (LOWER(contact_name));
CREATE INDEX idx_contact_email ON customer USING btree (LOWER(contact_email));
CREATE INDEX idx_phone ON customer USING btree (phone);

-- Trigger to automatically update updated_at on row modification
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_customer_updated_at
BEFORE UPDATE ON customer
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
```

**Note**: The `LOWER()` function in the index definitions enables efficient case-insensitive searches. PostgreSQL will use these indexes when executing queries like `WHERE LOWER(company_name) LIKE LOWER('%value%')`.

## JPA Entity Mapping (Java)

```java
package com.customer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer", indexes = {
    @Index(name = "idx_company_name", columnList = "companyName"),
    @Index(name = "idx_contact_name", columnList = "contactName"),
    @Index(name = "idx_contact_email", columnList = "contactEmail"),
    @Index(name = "idx_phone", columnList = "phone")
})
public class Customer {

    @Id
    @Column(name = "customer_id", length = 5, nullable = false)
    @NotBlank(message = "Customer ID is required")
    @Size(min = 5, max = 5, message = "Customer ID must be exactly 5 characters")
    private String customerId;

    @Column(name = "company_name", nullable = false, length = 40)
    @NotBlank(message = "Company name is required")
    @Size(max = 40, message = "Company name must not exceed 40 characters")
    private String companyName;

    @Column(name = "contact_name", length = 30)
    @Size(max = 30, message = "Contact name must not exceed 30 characters")
    private String contactName;

    @Column(name = "contact_title", length = 30)
    @Size(max = 30, message = "Contact title must not exceed 30 characters")
    private String contactTitle;

    @Column(name = "address", length = 60)
    @Size(max = 60, message = "Address must not exceed 60 characters")
    private String address;

    @Column(name = "city", length = 15)
    @Size(max = 15, message = "City must not exceed 15 characters")
    private String city;

    @Column(name = "region", length = 15)
    @Size(max = 15, message = "Region must not exceed 15 characters")
    private String region;

    @Column(name = "postal_code", length = 10)
    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String postalCode;

    @Column(name = "country", length = 15)
    @Size(max = 15, message = "Country must not exceed 15 characters")
    private String country;

    @Column(name = "phone", length = 24)
    @Size(max = 24, message = "Phone must not exceed 24 characters")
    private String phone;

    @Column(name = "fax", length = 24)
    @Size(max = 24, message = "Fax must not exceed 24 characters")
    private String fax;

    @Column(name = "contact_email", length = 255)
    @Email(message = "Contact email must be valid")
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors, getters, setters, equals, hashCode
}
```

## Data Transfer Objects (DTOs)

### CustomerRequest (for POST and PUT operations)

```java
package com.customer.dto;

import jakarta.validation.constraints.*;

public record CustomerRequest(
    @NotBlank(message = "Customer ID is required")
    @Size(min = 5, max = 5, message = "Customer ID must be exactly 5 characters")
    String customerId,

    @NotBlank(message = "Company name is required")
    @Size(max = 40, message = "Company name must not exceed 40 characters")
    String companyName,

    @Size(max = 30, message = "Contact name must not exceed 30 characters")
    String contactName,

    @Size(max = 30, message = "Contact title must not exceed 30 characters")
    String contactTitle,

    @Size(max = 60, message = "Address must not exceed 60 characters")
    String address,

    @Size(max = 15, message = "City must not exceed 15 characters")
    String city,

    @Size(max = 15, message = "Region must not exceed 15 characters")
    String region,

    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    String postalCode,

    @Size(max = 15, message = "Country must not exceed 15 characters")
    String country,

    @Size(max = 24, message = "Phone must not exceed 24 characters")
    String phone,

    @Size(max = 24, message = "Fax must not exceed 24 characters")
    String fax,

    @Email(message = "Contact email must be valid")
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    String contactEmail
) {}
```

### CustomerResponse (for GET operations)

```java
package com.customer.dto;

import java.time.LocalDateTime;

public record CustomerResponse(
    String customerId,
    String companyName,
    String contactName,
    String contactTitle,
    String address,
    String city,
    String region,
    String postalCode,
    String country,
    String phone,
    String fax,
    String contactEmail,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

## Validation Error Response

When validation fails, the API returns:

```json
{
  "timestamp": "2025-10-05T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "customerId",
      "rejectedValue": "ABC",
      "message": "Customer ID must be exactly 5 characters"
    },
    {
      "field": "companyName",
      "rejectedValue": "",
      "message": "Company name is required"
    }
  ]
}
```

## Summary

- **Single entity**: Customer (no relationships)
- **2 required fields**: customerId (5 chars, user-provided, unique), companyName (max 40 chars)
- **10 optional fields**: contactName, contactTitle, address, city, region, postalCode, country, phone, fax, contactEmail
- **2 metadata fields**: createdAt, updatedAt (auto-managed)
- **Unique constraint**: customerId (PRIMARY KEY)
- **Partial text search support** via indexed lowercase comparisons on companyName, contactName, contactEmail, phone
- **Last-write-wins concurrency** (no versioning)
- **Hard delete lifecycle** (permanent removal)
- **Bean Validation** enforced at API layer

Ready for contract generation (Phase 1 next step).
