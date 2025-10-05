package com.customer.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class SearchCustomerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupTestData() throws Exception {
        // Create test customers for searching
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "SRC01",
                        "companyName": "Acme Corporation",
                        "contactName": "Alice Anderson",
                        "contactEmail": "alice@acme.com",
                        "phone": "555-1000"
                    }
                    """));

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "SRC02",
                        "companyName": "Acme Industries",
                        "contactName": "Bob Brown",
                        "contactEmail": "bob@acme.com",
                        "phone": "555-2000"
                    }
                    """));
    }

    @Test
    void shouldSearchByCompanyNamePartialMatch() throws Exception {
        mockMvc.perform(get("/api/customers?companyName=acme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldSearchByCompanyNameCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/customers?companyName=ACME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldSearchByContactName() throws Exception {
        mockMvc.perform(get("/api/customers?contactName=Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contactName").value("Alice Anderson"));
    }

    @Test
    void shouldSearchByContactEmail() throws Exception {
        mockMvc.perform(get("/api/customers?contactEmail=bob@"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].contactEmail").value("bob@acme.com"));
    }

    @Test
    void shouldSearchByPhone() throws Exception {
        mockMvc.perform(get("/api/customers?phone=555-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].phone").value("555-1000"));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoResults() throws Exception {
        mockMvc.perform(get("/api/customers?companyName=NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
