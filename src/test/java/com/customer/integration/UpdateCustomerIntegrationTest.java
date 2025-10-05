package com.customer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class UpdateCustomerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldUpdateExistingCustomer() throws Exception {
        // Create customer
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "UPD11",
                        "companyName": "Original Name",
                        "contactName": "Original Contact"
                    }
                    """))
                .andExpect(status().isCreated());

        // Update customer
        mockMvc.perform(put("/api/customers/UPD11")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "companyName": "Updated Name",
                        "contactName": "Updated Contact",
                        "phone": "999-9999"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Updated Name"))
                .andExpect(jsonPath("$.contactName").value("Updated Contact"))
                .andExpect(jsonPath("$.phone").value("999-9999"));

        // Verify updatedAt timestamp changed
        mockMvc.perform(get("/api/customers/UPD11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Updated Name"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentCustomer() throws Exception {
        mockMvc.perform(put("/api/customers/NOEXIST")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "companyName": "Test"
                    }
                    """))
                .andExpect(status().isNotFound());
    }
}
