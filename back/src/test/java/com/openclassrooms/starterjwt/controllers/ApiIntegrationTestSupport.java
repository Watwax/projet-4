package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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
abstract class ApiIntegrationTestSupport {
    protected static final String MEMBER_EMAIL = "member@example.test";
    protected static final String PASSWORD = "password123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtils jwtUtils;

    @Autowired
    protected TeacherRepository teacherRepository;

    @Autowired
    protected SessionRepository sessionRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected SessionMapper sessionMapper;

    @Autowired
    protected TeacherMapper teacherMapper;

    @Autowired
    protected UserMapper userMapper;

    protected Teacher teacher;
    protected User member;
    protected String bearerToken;

    @BeforeEach
    void setUp() {
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
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(this.member.getId())
                .username(MEMBER_EMAIL)
                .firstName(this.member.getFirstName())
                .lastName(this.member.getLastName())
                .admin(false)
                .build();
        this.bearerToken = "Bearer " + this.jwtUtils.generateJwtToken(
                new UsernamePasswordAuthenticationToken(userDetails, null));
    }

    protected Map<String, Object> sessionRequest(String name, Long teacherId) {
        return Map.of(
                "name", name,
                "date", "2025-03-01T10:00:00.000+00:00",
                "teacher_id", teacherId,
                "description", "A useful integration-test session");
    }

    protected Session saveSession(String name) {
        return this.sessionRepository.save(Session.builder()
                .name(name)
                .date(new Date())
                .description("A useful integration-test session")
                .teacher(this.teacher)
                .users(new ArrayList<>())
                .build());
    }
}
