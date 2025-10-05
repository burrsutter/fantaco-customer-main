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
class SearchCustomersContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSearchByCompanyName() throws Exception {
        // Create test customer
        String requestBody = """
            {
                "customerId": "SCH01",
                "companyName": "Search Test Company"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        // Search by company name (partial match)
        mockMvc.perform(get("/api/customers?companyName=Search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value("SCH01"));
    }

    @Test
    void shouldSearchByContactName() throws Exception {
        String requestBody = """
            {
                "customerId": "SCH02",
                "companyName": "Company Two",
                "contactName": "Alice Smith"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/customers?contactName=Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].contactName").value("Alice Smith"));
    }

    @Test
    void shouldSearchByContactEmail() throws Exception {
        String requestBody = """
            {
                "customerId": "SCH03",
                "companyName": "Company Three",
                "contactEmail": "bob@test.com"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/customers?contactEmail=bob@test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].contactEmail").value("bob@test.com"));
    }

    @Test
    void shouldSearchByPhone() throws Exception {
        String requestBody = """
            {
                "customerId": "SCH04",
                "companyName": "Company Four",
                "phone": "555-1234"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/customers?phone=555"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].phone").value("555-1234"));
    }

    @Test
    void shouldReturnEmptyArrayForNoMatches() throws Exception {
        mockMvc.perform(get("/api/customers?companyName=NonExistentCompanyXYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
