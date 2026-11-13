package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldReturnAndDeleteTheAuthenticatedUsersAccount() throws Exception {
        this.mockMvc.perform(get("/api/user/{id}", this.member.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(MEMBER_EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());

        this.mockMvc.perform(delete("/api/user/{id}", this.member.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/user/{id}", this.member.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isUnauthorized());
    }
}
