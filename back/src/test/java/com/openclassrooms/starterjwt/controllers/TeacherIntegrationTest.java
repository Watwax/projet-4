package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeacherIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldListTeachersThroughAuthenticatedEndpoint() throws Exception {
        this.mockMvc.perform(get("/api/teacher").header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(this.teacher.getId()))
                .andExpect(jsonPath("$[0].firstName").value("Taylor"));
    }

    @Test
    void shouldFindTeacherByIdThroughAuthenticatedEndpoint() throws Exception {
        this.mockMvc.perform(get("/api/teacher/{id}", this.teacher.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void shouldReturnNotFoundWhenTeacherDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/api/teacher/99999").header("Authorization", this.bearerToken))
                .andExpect(status().isNotFound());
    }
}
