# Quickstart: Customer Master Data REST API

**Feature**: Customer Master Data REST API
**Date**: 2025-10-05
**Updated**: 2025-10-05 (schema revision)
**Purpose**: Manual testing scenarios and validation steps using curl commands

## Prerequisites

- API server running at `http://localhost:8080`
- `curl` command-line tool installed
- `jq` (optional) for formatting JSON responses

## Test Scenario 1: Create a New Customer (POST) - Full Data

**Acceptance Criterion**: Given an external system has a new customer, when it sends customer data with customerId and company name, then the system creates a new customer record.

### Command

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "ALFKI",
    "companyName": "Alfreds Futterkiste",
    "contactName": "Maria Anders",
    "contactTitle": "Sales Representative",
    "address": "Obere Str. 57",
    "city": "Berlin",
    "region": null,
    "postalCode": "12209",
    "country": "Germany",
    "phone": "030-0074321",
    "fax": "030-0076545",
    "contactEmail": "maria.anders@alfki.com"
  }'
```

### Expected Response

**Status Code**: `201 Created`

**Headers**:
```
Location: /api/customers/ALFKI
```

**Body**:
```json
{
  "customerId": "ALFKI",
  "companyName": "Alfreds Futterkiste",
  "contactName": "Maria Anders",
  "contactTitle": "Sales Representative",
  "address": "Obere Str. 57",
  "city": "Berlin",
  "region": null,
  "postalCode": "12209",
  "country": "Germany",
  "phone": "030-0074321",
  "fax": "030-0076545",
  "contactEmail": "maria.anders@alfki.com",
  "createdAt": "2025-10-05T10:30:00Z",
  "updatedAt": "2025-10-05T10:30:00Z"
}
```

### Validation
- ✅ `customerId` is user-provided (exactly 5 characters)
- ✅ All input fields are preserved in response
- ✅ `createdAt` and `updatedAt` timestamps are set to current time
- ✅ `Location` header contains URL of created resource

---

## Test Scenario 2: Create Customer with Minimal Data (POST)

**Acceptance Criterion**: Only customerId and companyName are required; all other fields are optional.

### Command

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "ANATR",
    "companyName": "Ana Trujillo Emparedados"
  }'
```

### Expected Response

**Status Code**: `201 Created`

**Body**:
```json
{
  "customerId": "ANATR",
  "companyName": "Ana Trujillo Emparedados",
  "contactName": null,
  "contactTitle": null,
  "address": null,
  "city": null,
  "region": null,
  "postalCode": null,
  "country": null,
  "phone": null,
  "fax": null,
  "contactEmail": null,
  "createdAt": "2025-10-05T10:32:00Z",
  "updatedAt": "2025-10-05T10:32:00Z"
}
```

### Validation
- ✅ Only required fields provided
- ✅ Optional fields are null
- ✅ Customer created successfully

---

## Test Scenario 3: Retrieve Customer by ID (GET)

**Acceptance Criterion**: Given a customer exists with customerId "ALFKI", when an external system requests customer data by customerId "ALFKI", then the system returns all customer information.

### Command

```bash
curl -X GET http://localhost:8080/api/customers/ALFKI
```

### Expected Response

**Status Code**: `200 OK`

**Body**:
```json
{
  "customerId": "ALFKI",
  "companyName": "Alfreds Futterkiste",
  "contactName": "Maria Anders",
  "contactTitle": "Sales Representative",
  "address": "Obere Str. 57",
  "city": "Berlin",
  "region": null,
  "postalCode": "12209",
  "country": "Germany",
  "phone": "030-0074321",
  "fax": "030-0076545",
  "contactEmail": "maria.anders@alfki.com",
  "createdAt": "2025-10-05T10:30:00Z",
  "updatedAt": "2025-10-05T10:30:00Z"
}
```

### Validation
- ✅ Customer data matches what was created in Scenario 1
- ✅ All fields present in response

---

## Test Scenario 4: Search by Contact Email (GET with Query)

**Acceptance Criterion**: Given a customer exists with email "maria.anders@alfki.com", when an external system searches by contact email, then the system returns matching customer records.

### Command (Exact match)

```bash
curl -X GET "http://localhost:8080/api/customers?contactEmail=maria.anders@alfki.com"
```

### Command (Partial match - case insensitive)

```bash
curl -X GET "http://localhost:8080/api/customers?contactEmail=maria@alfki"
```

### Expected Response

**Status Code**: `200 OK`

**Body**:
```json
[
  {
    "customerId": "ALFKI",
    "companyName": "Alfreds Futterkiste",
    "contactName": "Maria Anders",
    "contactTitle": "Sales Representative",
    "address": "Obere Str. 57",
    "city": "Berlin",
    "region": null,
    "postalCode": "12209",
    "country": "Germany",
    "phone": "030-0074321",
    "fax": "030-0076545",
    "contactEmail": "maria.anders@alfki.com",
    "createdAt": "2025-10-05T10:30:00Z",
    "updatedAt": "2025-10-05T10:30:00Z"
  }
]
```

### Validation
- ✅ Response is an array (even for single result)
- ✅ Partial match works (e.g., "maria@alfki" matches "maria.anders@alfki.com")
- ✅ Case-insensitive (e.g., "MARIA@ALFKI" also matches)

---

## Test Scenario 5: Update Customer (PUT)

**Acceptance Criterion**: Given a customer's data has changed, when an external system updates the customer record, then the system persists the change and confirms the update.

### Setup: Create another customer first

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "ANTON",
    "companyName": "Antonio Moreno Taquería",
    "contactName": "Antonio Moreno",
    "contactTitle": "Owner",
    "address": "Mataderos 2312",
    "city": "México D.F.",
    "region": null,
    "postalCode": "05023",
    "country": "Mexico",
    "phone": "(5) 555-3932",
    "fax": null,
    "contactEmail": "antonio@moreno.com"
  }'
```

### Command: Update customer (change title and phone)

```bash
curl -X PUT http://localhost:8080/api/customers/ANTON \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Antonio Moreno Taquería",
    "contactName": "Antonio Moreno",
    "contactTitle": "CEO",
    "address": "Mataderos 2312",
    "city": "México D.F.",
    "region": null,
    "postalCode": "05023",
    "country": "Mexico",
    "phone": "(5) 555-3999",
    "fax": null,
    "contactEmail": "antonio@moreno.com"
  }'
```

### Expected Response

**Status Code**: `200 OK`

**Body**:
```json
{
  "customerId": "ANTON",
  "companyName": "Antonio Moreno Taquería",
  "contactName": "Antonio Moreno",
  "contactTitle": "CEO",
  "address": "Mataderos 2312",
  "city": "México D.F.",
  "region": null,
  "postalCode": "05023",
  "country": "Mexico",
  "phone": "(5) 555-3999",
  "fax": null,
  "contactEmail": "antonio@moreno.com",
  "createdAt": "2025-10-05T10:35:00Z",
  "updatedAt": "2025-10-05T10:40:00Z"
}
```

### Validation
- ✅ Contact title changed from "Owner" to "CEO"
- ✅ Phone number changed from "(5) 555-3932" to "(5) 555-3999"
- ✅ `updatedAt` timestamp is more recent than `createdAt`
- ✅ Other fields unchanged
- ✅ Customer ID cannot be changed (it's in the path)

---

## Test Scenario 6: Delete Customer (DELETE)

**Acceptance Criterion**: Given a customer is no longer active, when an external system requests deletion of that customer, then the system removes the customer record and confirms deletion.

### Command

```bash
curl -X DELETE http://localhost:8080/api/customers/ANTON \
  -w "\nHTTP Status: %{http_code}\n"
```

### Expected Response

**Status Code**: `204 No Content`

**Body**: (empty)

### Validation: Verify deletion

```bash
curl -X GET http://localhost:8080/api/customers/ANTON \
  -w "\nHTTP Status: %{http_code}\n"
```

**Expected**: `404 Not Found`

```json
{
  "timestamp": "2025-10-05T10:45:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Customer with ID ANTON not found"
}
```

### Validation
- ✅ DELETE returns `204 No Content`
- ✅ Subsequent GET returns `404 Not Found`
- ✅ Record is permanently deleted (hard delete confirmed)

---

## Test Scenario 7: Search by Company Name (Partial Match)

**Acceptance Criterion**: When an external system searches by company name, the system returns all matching customer records using partial, case-insensitive matching.

### Setup: Create multiple customers for testing

```bash
# Customer 1 already exists: "Alfreds Futterkiste"

# Create second customer
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "AROUT",
    "companyName": "Around the Horn",
    "contactName": "Thomas Hardy",
    "contactTitle": "Sales Representative",
    "address": "120 Hanover Sq.",
    "city": "London",
    "region": null,
    "postalCode": "WA1 1DP",
    "country": "UK",
    "phone": "(171) 555-7788",
    "fax": "(171) 555-6750",
    "contactEmail": "thomas@aroundthehorn.com"
  }'

# Create third customer with similar name
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "BERGS",
    "companyName": "Berglunds snabbköp",
    "contactName": "Christina Berglund",
    "contactTitle": "Order Administrator",
    "address": "Berguvsvägen 8",
    "city": "Luleå",
    "region": null,
    "postalCode": "S-958 22",
    "country": "Sweden",
    "phone": "0921-12 34 65",
    "fax": "0921-12 34 67",
    "contactEmail": "christina@berglunds.se"
  }'
```

### Command: Search by partial company name

```bash
curl -X GET "http://localhost:8080/api/customers?companyName=berg"
```

### Expected Response

**Status Code**: `200 OK`

**Body**:
```json
[
  {
    "customerId": "BERGS",
    "companyName": "Berglunds snabbköp",
    "contactName": "Christina Berglund",
    "contactTitle": "Order Administrator",
    "address": "Berguvsvägen 8",
    "city": "Luleå",
    "region": null,
    "postalCode": "S-958 22",
    "country": "Sweden",
    "phone": "0921-12 34 65",
    "fax": "0921-12 34 67",
    "contactEmail": "christina@berglunds.se",
    "createdAt": "2025-10-05T10:52:00Z",
    "updatedAt": "2025-10-05T10:52:00Z"
  }
]
```

### Validation
- ✅ Partial match works (e.g., "berg" matches "Berglunds snabbköp")
- ✅ Case-insensitive search confirmed (lowercase "berg" matches mixed-case "Berglunds")

---

## Edge Case Tests

### Edge Case 1: Create Customer with Invalid Customer ID (Too Short)

**Command**:
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "ABC",
    "companyName": "Test Company"
  }'
```

**Expected Response**: `400 Bad Request`

```json
{
  "timestamp": "2025-10-05T11:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "customerId",
      "rejectedValue": "ABC",
      "message": "Customer ID must be exactly 5 characters"
    }
  ]
}
```

### Edge Case 2: Create Customer with Missing Required Field (companyName)

**Command**:
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "TEST1"
  }'
```

**Expected Response**: `400 Bad Request`

```json
{
  "timestamp": "2025-10-05T11:02:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "companyName",
      "rejectedValue": null,
      "message": "Company name is required"
    }
  ]
}
```

### Edge Case 3: Create Customer with Duplicate ID

**Command**:
```bash
# Try to create another customer with existing ID
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "ALFKI",
    "companyName": "Another Company"
  }'
```

**Expected Response**: `409 Conflict`

```json
{
  "timestamp": "2025-10-05T11:05:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Customer with ID ALFKI already exists"
}
```

### Edge Case 4: Get Non-Existent Customer

**Command**:
```bash
curl -X GET http://localhost:8080/api/customers/XXXXX
```

**Expected Response**: `404 Not Found`

```json
{
  "timestamp": "2025-10-05T11:07:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Customer with ID XXXXX not found"
}
```

### Edge Case 5: Update Non-Existent Customer

**Command**:
```bash
curl -X PUT http://localhost:8080/api/customers/XXXXX \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Test Corp"
  }'
```

**Expected Response**: `404 Not Found`

### Edge Case 6: Delete Non-Existent Customer

**Command**:
```bash
curl -X DELETE http://localhost:8080/api/customers/XXXXX
```

**Expected Response**: `404 Not Found`

### Edge Case 7: Search with No Results

**Command**:
```bash
curl -X GET "http://localhost:8080/api/customers?companyName=NonExistentCompanyXYZ"
```

**Expected Response**: `200 OK`

```json
[]
```

### Edge Case 8: Field Length Validation

**Command**:
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "TEST2",
    "companyName": "This company name is way too long and exceeds forty characters limit",
    "contactName": "This name is also too long for thirty characters maximum"
  }'
```

**Expected Response**: `400 Bad Request`

```json
{
  "timestamp": "2025-10-05T11:10:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "companyName",
      "rejectedValue": "This company name is way too long and exceeds forty characters limit",
      "message": "Company name must not exceed 40 characters"
    },
    {
      "field": "contactName",
      "rejectedValue": "This name is also too long for thirty characters maximum",
      "message": "Contact name must not exceed 30 characters"
    }
  ]
}
```

---

## Complete Test Suite Summary

| Test Scenario                               | HTTP Method | Path                        | Expected Status | Validated |
|---------------------------------------------|-------------|-----------------------------|-----------------|-----------|
| Create customer (full data)                 | POST        | /api/customers              | 201             | [ ]       |
| Create customer (minimal data)              | POST        | /api/customers              | 201             | [ ]       |
| Get customer by ID                          | GET         | /api/customers/{id}         | 200             | [ ]       |
| Search by contact email                     | GET         | /api/customers?contactEmail | 200             | [ ]       |
| Update customer                             | PUT         | /api/customers/{id}         | 200             | [ ]       |
| Delete customer                             | DELETE      | /api/customers/{id}         | 204             | [ ]       |
| Search by company name (partial)            | GET         | /api/customers?companyName  | 200             | [ ]       |
| Create with invalid ID (too short)          | POST        | /api/customers              | 400             | [ ]       |
| Create with missing required field          | POST        | /api/customers              | 400             | [ ]       |
| Create with duplicate ID                    | POST        | /api/customers              | 409             | [ ]       |
| Get non-existent customer                   | GET         | /api/customers/{id}         | 404             | [ ]       |
| Update non-existent customer                | PUT         | /api/customers/{id}         | 404             | [ ]       |
| Delete non-existent customer                | DELETE      | /api/customers/{id}         | 404             | [ ]       |
| Search with no results                      | GET         | /api/customers?companyName  | 200 (empty)     | [ ]       |
| Field length validation                     | POST        | /api/customers              | 400             | [ ]       |

---

## Notes for Testers

1. **Customer IDs**: Must be exactly 5 characters (e.g., "ALFKI", "ANATR")
2. **Required fields**: Only `customerId` and `companyName` are required
3. **Optional fields**: All other fields can be null or omitted
4. **Partial matching**: All text searches support partial matching (e.g., "berg" matches "Berglunds")
5. **Case insensitivity**: All text searches are case-insensitive
6. **Unique constraint**: Customer ID must be unique (409 Conflict if duplicate)
7. **No pagination**: Currently returns all results (FR-021 deferred)
8. **No authentication**: API is open to all callers (FR-014 clarification)
9. **Hard delete**: DELETE permanently removes records

---

## Success Criteria

All test scenarios pass with expected status codes and response bodies. Edge cases are handled gracefully with appropriate error messages.

## README.md curl Examples

These curl commands should also be included in the project README.md for quick reference:

### Create Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"customerId":"ALFKI","companyName":"Alfreds Futterkiste"}'
```

### Get Customer by ID
```bash
curl http://localhost:8080/api/customers/ALFKI
```

### Search Customers
```bash
curl "http://localhost:8080/api/customers?companyName=Alfreds"
```

### Update Customer
```bash
curl -X PUT http://localhost:8080/api/customers/ALFKI \
  -H "Content-Type: application/json" \
  -d '{"companyName":"Alfreds Futterkiste GmbH"}'
```

### Delete Customer
```bash
curl -X DELETE http://localhost:8080/api/customers/ALFKI
```
