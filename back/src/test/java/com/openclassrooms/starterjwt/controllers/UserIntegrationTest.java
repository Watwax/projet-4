package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldReturnTheAuthenticatedUsersAccount() throws Exception {
        this.mockMvc.perform(get("/api/user/{id}", this.member.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(MEMBER_EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldDeleteTheAuthenticatedUsersAccount() throws Exception {
        this.mockMvc.perform(delete("/api/user/{id}", this.member.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk());
        assertFalse(this.userRepository.existsById(this.member.getId()));
    }

    @Test
    void shouldRejectTokenWhoseUserNoLongerExists() throws Exception {
        this.userRepository.delete(this.member);
        this.mockMvc.perform(get("/api/user/{id}", this.member.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isUnauthorized());
    }
}
