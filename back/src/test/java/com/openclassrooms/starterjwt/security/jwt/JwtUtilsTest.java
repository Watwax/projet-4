package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {
    private static final String SECRET = Base64.getEncoder()
            .encodeToString("test-key-for-hs512".repeat(4).getBytes(StandardCharsets.UTF_8));
    private static final String OTHER_SECRET = Base64.getEncoder()
            .encodeToString("other-key-for-hs512".repeat(4).getBytes(StandardCharsets.UTF_8));

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        this.jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(this.jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(this.jwtUtils, "jwtExpirationMs", 60_000);
    }

    @Test
    void shouldGenerateValidateAndReadJwtToken() {
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L)
                .username("member@example.test")
                .build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null);

        String token = this.jwtUtils.generateJwtToken(authentication);

        assertTrue(this.jwtUtils.validateJwtToken(token));
        assertEquals("member@example.test", this.jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void shouldRejectInvalidJwtTokens() {
        assertFalse(this.jwtUtils.validateJwtToken(null));
        assertFalse(this.jwtUtils.validateJwtToken("not-a-jwt"));
        assertFalse(this.jwtUtils.validateJwtToken(
                this.createToken(OTHER_SECRET, new Date(System.currentTimeMillis() + 60_000))));
        assertFalse(this.jwtUtils.validateJwtToken(
                this.createToken(SECRET, new Date(System.currentTimeMillis() - 1_000))));
    }

    @Test
    void shouldRejectUnsupportedUnsignedJwt() {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"member@example.test\"}".getBytes(StandardCharsets.UTF_8));

        assertFalse(this.jwtUtils.validateJwtToken(header + "." + payload + "."));
    }

    private String createToken(String secret, Date expiration) {
        return Jwts.builder()
                .setSubject("member@example.test")
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
