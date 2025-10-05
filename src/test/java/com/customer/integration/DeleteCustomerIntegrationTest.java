package com.customer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class DeleteCustomerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldHardDeleteCustomer() throws Exception {
        // Create customer
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "DEL11",
                        "companyName": "To Be Deleted"
                    }
                    """))
                .andExpect(status().isCreated());

        // Delete customer
        mockMvc.perform(delete("/api/customers/DEL11"))
                .andExpect(status().isNoContent());

        // Verify subsequent GET returns 404
        mockMvc.perform(get("/api/customers/DEL11"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/NOEXIST"))
                .andExpect(status().isNotFound());
    }
}
