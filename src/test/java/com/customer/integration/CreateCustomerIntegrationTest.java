package com.customer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CreateCustomerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateCustomerWithFullData() throws Exception {
        String requestBody = """
            {
                "customerId": "INT01",
                "companyName": "Integration Test Company",
                "contactName": "John Integration",
                "contactTitle": "CEO",
                "address": "123 Test St",
                "city": "Test City",
                "region": "TC",
                "postalCode": "12345",
                "country": "TestLand",
                "phone": "111-222-3333",
                "fax": "111-222-4444",
                "contactEmail": "john@integration.com"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("INT01"))
                .andExpect(jsonPath("$.companyName").value("Integration Test Company"))
                .andExpect(jsonPath("$.contactName").value("John Integration"))
                .andExpect(jsonPath("$.contactTitle").value("CEO"))
                .andExpect(jsonPath("$.address").value("123 Test St"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.region").value("TC"))
                .andExpect(jsonPath("$.postalCode").value("12345"))
                .andExpect(jsonPath("$.country").value("TestLand"))
                .andExpect(jsonPath("$.phone").value("111-222-3333"))
                .andExpect(jsonPath("$.fax").value("111-222-4444"))
                .andExpect(jsonPath("$.contactEmail").value("john@integration.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        // Verify it persisted to database
        mockMvc.perform(get("/api/customers/INT01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("INT01"));
    }

    @Test
    void shouldCreateCustomerWithMinimalData() throws Exception {
        String requestBody = """
            {
                "customerId": "INT02",
                "companyName": "Minimal Company"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("INT02"))
                .andExpect(jsonPath("$.companyName").value("Minimal Company"))
                .andExpect(jsonPath("$.contactName").isEmpty())
                .andExpect(jsonPath("$.createdAt").exists());

        // Verify database persistence
        mockMvc.perform(get("/api/customers/INT02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Minimal Company"));
    }
}
