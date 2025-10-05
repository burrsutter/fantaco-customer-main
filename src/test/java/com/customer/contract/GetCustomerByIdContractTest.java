package com.customer.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GetCustomerByIdContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnCustomerById() throws Exception {
        // Create a customer first
        String requestBody = """
            {
                "customerId": "GET01",
                "companyName": "Get Test Company",
                "contactName": "Jane Doe",
                "contactEmail": "jane@test.com"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        // Retrieve it
        mockMvc.perform(get("/api/customers/GET01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("GET01"))
                .andExpect(jsonPath("$.companyName").value("Get Test Company"))
                .andExpect(jsonPath("$.contactName").value("Jane Doe"))
                .andExpect(jsonPath("$.contactEmail").value("jane@test.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturn404ForNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/XXXXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
