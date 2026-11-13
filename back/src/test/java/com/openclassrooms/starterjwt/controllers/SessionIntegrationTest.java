package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.openclassrooms.starterjwt.models.Session;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldCreateListAndFindSessionsThroughAuthenticatedEndpoints() throws Exception {
        Map<String, Object> sessionRequest = this.sessionRequest("Morning yoga", this.teacher.getId());
        MvcResult createResult = this.mockMvc.perform(post("/api/session")
                        .header("Authorization", this.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning yoga"))
                .andExpect(jsonPath("$.teacher_id").value(this.teacher.getId()))
                .andReturn();

        JsonNode created = this.objectMapper.readTree(createResult.getResponse().getContentAsString());
        long sessionId = created.get("id").asLong();

        this.mockMvc.perform(get("/api/session/{id}", sessionId)
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("A useful integration-test session"));

        this.mockMvc.perform(get("/api/session").header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sessionId));
    }

    @Test
    void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/api/session/99999")
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectSessionWithInvalidRequiredFields() throws Exception {
        this.mockMvc.perform(post("/api/session")
                        .header("Authorization", this.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(Map.of(
                                "name", "",
                                "date", "2025-03-01T10:00:00.000+00:00",
                                "description", "A session",
                                "teacher_id", this.teacher.getId()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateAndDeleteSessionThroughAuthenticatedEndpoints() throws Exception {
        Session session = this.saveSession("Original name");
        Map<String, Object> updatedRequest = this.sessionRequest("Updated name", this.teacher.getId());

        this.mockMvc.perform(put("/api/session/{id}", session.getId())
                        .header("Authorization", this.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Updated name"));

        this.mockMvc.perform(delete("/api/session/{id}", session.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/session/{id}", session.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddAndRemoveSessionParticipantAndRejectDuplicateChanges() throws Exception {
        Session session = this.saveSession("Participation session");
        String participationUrl = "/api/session/" + session.getId() + "/participate/" + this.member.getId();

        this.mockMvc.perform(post(participationUrl).header("Authorization", this.bearerToken))
                .andExpect(status().isOk());

        this.mockMvc.perform(post(participationUrl).header("Authorization", this.bearerToken))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(delete(participationUrl).header("Authorization", this.bearerToken))
                .andExpect(status().isOk());

        this.mockMvc.perform(delete(participationUrl).header("Authorization", this.bearerToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldTranslateMalformedResourceIdsToBadRequest() throws Exception {
        this.mockMvc.perform(get("/api/session/not-a-number")
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isBadRequest());
    }
}
