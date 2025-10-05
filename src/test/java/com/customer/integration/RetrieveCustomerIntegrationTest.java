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
class RetrieveCustomerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRetrieveCustomerById() throws Exception {
        // Create customer first
        String requestBody = """
            {
                "customerId": "RET01",
                "companyName": "Retrieve Test Co",
                "contactName": "Jane Retrieve",
                "contactEmail": "jane@retrieve.com",
                "phone": "555-0001"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        // Retrieve and verify all fields
        mockMvc.perform(get("/api/customers/RET01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("RET01"))
                .andExpect(jsonPath("$.companyName").value("Retrieve Test Co"))
                .andExpect(jsonPath("$.contactName").value("Jane Retrieve"))
                .andExpect(jsonPath("$.contactEmail").value("jane@retrieve.com"))
                .andExpect(jsonPath("$.phone").value("555-0001"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }
}
