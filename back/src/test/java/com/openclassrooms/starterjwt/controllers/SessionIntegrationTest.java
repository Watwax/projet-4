package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Session;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SessionIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldCreateSessionThroughAuthenticatedEndpoint() throws Exception {
        Map<String, Object> request = this.sessionRequest("Morning yoga", this.teacher.getId());

        this.mockMvc.perform(post("/api/session")
                        .header("Authorization", this.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning yoga"))
                .andExpect(jsonPath("$.teacher_id").value(this.teacher.getId()));
    }

    @Test
    void shouldFindSessionById() throws Exception {
        Session session = this.saveSession("Morning yoga");

        this.mockMvc.perform(get("/api/session/{id}", session.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("A useful integration-test session"));
    }

    @Test
    void shouldListSessions() throws Exception {
        Session session = this.saveSession("Morning yoga");

        this.mockMvc.perform(get("/api/session").header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(session.getId()));
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
    void shouldUpdateSession() throws Exception {
        Session session = this.saveSession("Original name");
        Map<String, Object> request = this.sessionRequest("Updated name", this.teacher.getId());

        this.mockMvc.perform(put("/api/session/{id}", session.getId())
                        .header("Authorization", this.bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Updated name"));
    }

    @Test
    void shouldDeleteSession() throws Exception {
        Session session = this.saveSession("Session to delete");

        this.mockMvc.perform(delete("/api/session/{id}", session.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk());

        assertFalse(this.sessionRepository.existsById(session.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/api/session/99999")
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddSessionParticipant() throws Exception {
        Session session = this.saveSession("Participation session");
        String participationUrl = this.participationUrl(session);

        this.mockMvc.perform(post(participationUrl).header("Authorization", this.bearerToken))
                .andExpect(status().isOk());

        assertTrue(this.sessionRepository.findById(session.getId()).orElseThrow()
                .getUsers().stream().anyMatch(user -> user.getId().equals(this.member.getId())));
    }

    @Test
    void shouldRejectDuplicateSessionParticipation() throws Exception {
        Session session = this.saveSessionWithMember("Already participating");

        this.mockMvc.perform(post(this.participationUrl(session)).header("Authorization", this.bearerToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRemoveSessionParticipant() throws Exception {
        Session session = this.saveSessionWithMember("Participation to remove");

        this.mockMvc.perform(delete(this.participationUrl(session)).header("Authorization", this.bearerToken))
                .andExpect(status().isOk());

        assertFalse(this.sessionRepository.findById(session.getId()).orElseThrow()
                .getUsers().stream().anyMatch(user -> user.getId().equals(this.member.getId())));
    }

    @Test
    void shouldRejectRemovingNonParticipatingUser() throws Exception {
        Session session = this.saveSession("No participants");

        this.mockMvc.perform(delete(this.participationUrl(session)).header("Authorization", this.bearerToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldTranslateMalformedResourceIdsToBadRequest() throws Exception {
        this.mockMvc.perform(get("/api/session/not-a-number")
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isBadRequest());
    }

    private Session saveSessionWithMember(String name) {
        Session session = this.saveSession(name);
        session.setUsers(new ArrayList<>(List.of(this.member)));
        return this.sessionRepository.save(session);
    }

    private String participationUrl(Session session) {
        return "/api/session/" + session.getId() + "/participate/" + this.member.getId();
    }
}
