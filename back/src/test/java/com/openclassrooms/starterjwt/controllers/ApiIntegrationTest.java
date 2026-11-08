package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.docker.compose.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:yoga-test;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "oc.app.jwtSecret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWYwMTIzNDU2Nzg5YWJjZGVmMDEyMzQ1Njc4OWFiY2RlZg==",
        "oc.app.jwtExpirationMs=3600000"
})
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTest {
    private static final String MEMBER_EMAIL = "member@example.test";
    private static final String PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private UserMapper userMapper;

    private Teacher teacher;
    private User member;
    private String bearerToken;

    @BeforeEach
    void setUp() throws Exception {
        this.sessionRepository.deleteAll();
        this.userRepository.deleteAll();
        this.teacherRepository.deleteAll();

        this.teacher = this.teacherRepository.save(new Teacher()
                .setFirstName("Taylor")
                .setLastName("Smith"));
        this.member = this.userRepository.save(new User(
                MEMBER_EMAIL,
                "Member",
                "Alex",
                this.passwordEncoder.encode(PASSWORD),
                false));
        this.bearerToken = this.login(MEMBER_EMAIL, PASSWORD);
    }

    @Test
    void shouldRegisterAndLoginThroughPublicAuthenticationEndpoints() throws Exception {
        Map<String, String> signup = Map.of(
                "email", "new-member@example.test",
                "firstName", "Jamie",
                "lastName", "Tester",
                "password", "new-password");

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(signup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(signup)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(Map.of(
                                "email", signup.get("email"),
                                "password", signup.get("password")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(signup.get("email")))
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

    @Test
    void shouldRequireAuthenticationForProtectedEndpoints() throws Exception {
        this.mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isUnauthorized());

        this.mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldListAndFindTeachersThroughAuthenticatedEndpoints() throws Exception {
        this.mockMvc.perform(get("/api/teacher").header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(this.teacher.getId()))
                .andExpect(jsonPath("$[0].firstName").value("Taylor"));

        this.mockMvc.perform(get("/api/teacher/{id}", this.teacher.getId())
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"));

        this.mockMvc.perform(get("/api/teacher/99999").header("Authorization", this.bearerToken))
                .andExpect(status().isNotFound());
    }

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

    @Test
    void shouldTranslateMalformedResourceIdsToBadRequest() throws Exception {
        this.mockMvc.perform(get("/api/session/not-a-number")
                        .header("Authorization", this.bearerToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldMapTeachersAndHandleNullMapperInputs() {
        TeacherDto teacherDto = this.teacherMapper.toDto(this.teacher);

        assertEquals(this.teacher.getId(), teacherDto.getId());
        assertEquals(this.teacher.getFirstName(), teacherDto.getFirstName());
        assertEquals(this.teacher.getLastName(), teacherDto.getLastName());
        assertNotNull(this.teacherMapper.toEntity(teacherDto));
        assertNull(this.teacherMapper.toDto((Teacher) null));
        assertNull(this.teacherMapper.toEntity((TeacherDto) null));
        assertEquals(2, this.teacherMapper.toDto(Arrays.asList(this.teacher, null)).size());
        assertEquals(2, this.teacherMapper.toEntity(Arrays.asList(teacherDto, null)).size());
        assertNull(this.teacherMapper.toDto((java.util.List<Teacher>) null));
        assertNull(this.teacherMapper.toEntity((java.util.List<TeacherDto>) null));
    }

    @Test
    void shouldMapUsersAndHandleNullMapperInputs() {
        UserDto userDto = this.userMapper.toDto(this.member);

        assertEquals(this.member.getId(), userDto.getId());
        assertEquals(this.member.getEmail(), userDto.getEmail());
        assertEquals(this.member.getFirstName(), userDto.getFirstName());
        assertEquals(this.member.getLastName(), userDto.getLastName());
        assertNotNull(this.userMapper.toEntity(userDto));
        assertNull(this.userMapper.toDto((User) null));
        assertNull(this.userMapper.toEntity((UserDto) null));
        assertEquals(2, this.userMapper.toDto(Arrays.asList(this.member, null)).size());
        assertEquals(2, this.userMapper.toEntity(Arrays.asList(userDto, null)).size());
        assertNull(this.userMapper.toDto((java.util.List<User>) null));
        assertNull(this.userMapper.toEntity((java.util.List<UserDto>) null));
    }

    @Test
    void shouldMapSessionRelationshipsAndHandleMissingParticipants() {
        SessionDto sessionDto = new SessionDto(
                null,
                "Mapped session",
                new Date(),
                this.teacher.getId(),
                "A mapped session",
                Arrays.asList(this.member.getId(), Long.MAX_VALUE),
                null,
                null);

        Session mapped = this.sessionMapper.toEntity(sessionDto);
        assertEquals(this.teacher.getId(), mapped.getTeacher().getId());
        assertEquals(this.member.getId(), mapped.getUsers().get(0).getId());
        assertNull(mapped.getUsers().get(1));

        SessionDto mappedDto = this.sessionMapper.toDto(this.saveSession("Mapped session"));
        assertEquals(this.teacher.getId(), mappedDto.getTeacher_id());
        assertEquals(0, mappedDto.getUsers().size());
    }

    @Test
    void shouldMapSessionsWithoutOptionalRelationships() {
        SessionDto sessionDto = new SessionDto(
                null,
                "Unassigned session",
                new Date(),
                null,
                "No optional relationships",
                null,
                null,
                null);

        Session mapped = this.sessionMapper.toEntity(sessionDto);
        assertNull(mapped.getTeacher());
        assertEquals(0, mapped.getUsers().size());

        Session entity = new Session()
                .setName("Unassigned session")
                .setDate(new Date())
                .setDescription("No optional relationships")
                .setTeacher(null)
                .setUsers(null);
        SessionDto mappedDto = this.sessionMapper.toDto(entity);
        assertNull(mappedDto.getTeacher_id());
        assertEquals(0, mappedDto.getUsers().size());
    }

    @Test
    void shouldMapSessionListsAndHandleNullMapperInputs() {
        Session session = this.saveSession("Listed session");
        SessionDto sessionDto = this.sessionMapper.toDto(session);

        assertNull(this.sessionMapper.toDto((Session) null));
        assertNull(this.sessionMapper.toEntity((SessionDto) null));
        assertEquals(2, this.sessionMapper.toDto(Arrays.asList(session, null)).size());
        assertEquals(2, this.sessionMapper.toEntity(Arrays.asList(sessionDto, null)).size());
        assertNull(this.sessionMapper.toDto((java.util.List<Session>) null));
        assertNull(this.sessionMapper.toEntity((java.util.List<SessionDto>) null));
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = this.objectMapper.readTree(result.getResponse().getContentAsString());
        return "Bearer " + response.get("token").asText();
    }

    private Map<String, Object> sessionRequest(String name, Long teacherId) {
        return Map.of(
                "name", name,
                "date", "2025-03-01T10:00:00.000+00:00",
                "teacher_id", teacherId,
                "description", "A useful integration-test session");
    }

    private Session saveSession(String name) {
        return this.sessionRepository.save(Session.builder()
                .name(name)
                .date(new Date())
                .description("A useful integration-test session")
                .teacher(this.teacher)
                .users(new ArrayList<>())
                .build());
    }
}
