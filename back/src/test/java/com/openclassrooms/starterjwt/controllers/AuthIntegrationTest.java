package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldRegisterUserThroughPublicAuthenticationEndpoint() throws Exception {
        Map<String, String> signup = signupRequest(
                "new-member@example.test", "Jamie", "Tester", "new-password");

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(signup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() throws Exception {
        Map<String, String> signup = signupRequest(
                MEMBER_EMAIL, "Alex", "Member", PASSWORD);

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(signup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    void shouldLoginThroughPublicAuthenticationEndpoint() throws Exception {
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(Map.of(
                                "email", MEMBER_EMAIL,
                                "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(MEMBER_EMAIL))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    void shouldRejectInvalidSignupPayload() throws Exception {
        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(Map.of(
                                "email", "invalid-email",
                                "firstName", "",
                                "lastName", "Member",
                                "password", "short"))))
                .andExpect(status().isBadRequest());
    }

    private Map<String, String> signupRequest(
            String email, String firstName, String lastName, String password) {
        return Map.of(
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "password", password);
    }
}
