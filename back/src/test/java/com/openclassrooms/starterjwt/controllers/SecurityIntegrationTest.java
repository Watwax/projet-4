package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldRequireAuthenticationForProtectedEndpoints() throws Exception {
        this.mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInvalidBearerToken() throws Exception {
        this.mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
