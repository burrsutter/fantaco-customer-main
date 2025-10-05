package com.customer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ValidationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectCustomerIdNot5Characters() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "ABC",
                        "companyName": "Test Company"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldRejectMissingCompanyName() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "VAL01"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCompanyNameExceeding40Chars() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "VAL02",
                        "companyName": "This company name is way too long and exceeds the forty character limit"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidEmailFormat() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "VAL03",
                        "companyName": "Test Company",
                        "contactEmail": "invalid-email"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectFieldsExceedingMaxLength() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "VAL04",
                        "companyName": "Test",
                        "contactName": "This name is way too long for the thirty character maximum limit",
                        "city": "ThisCityNameIsTooLong"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }
}
