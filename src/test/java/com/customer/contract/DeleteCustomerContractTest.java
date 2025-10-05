package com.customer.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeleteCustomerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDeleteExistingCustomer() throws Exception {
        // Create customer
        String createBody = """
            {
                "customerId": "DEL01",
                "companyName": "Delete Test Company"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isCreated());

        // Delete customer
        mockMvc.perform(delete("/api/customers/DEL01"))
                .andExpect(status().isNoContent());

        // Verify customer is deleted
        mockMvc.perform(get("/api/customers/DEL01"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/XXXXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
