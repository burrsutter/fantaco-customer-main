package com.customer.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UpdateCustomerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldUpdateExistingCustomer() throws Exception {
        // Create customer
        String createBody = """
            {
                "customerId": "UPD01",
                "companyName": "Original Company",
                "contactName": "Original Name"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andExpect(status().isCreated());

        // Update customer
        String updateBody = """
            {
                "companyName": "Updated Company",
                "contactName": "Updated Name",
                "phone": "999-8888"
            }
            """;

        mockMvc.perform(put("/api/customers/UPD01")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("UPD01"))
                .andExpect(jsonPath("$.companyName").value("Updated Company"))
                .andExpect(jsonPath("$.contactName").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("999-8888"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentCustomer() throws Exception {
        String updateBody = """
            {
                "companyName": "Test Company"
            }
            """;

        mockMvc.perform(put("/api/customers/XXXXX")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldRejectUpdateWithInvalidData() throws Exception {
        String updateBody = """
            {
                "companyName": ""
            }
            """;

        mockMvc.perform(put("/api/customers/UPD02")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isBadRequest());
    }
}
