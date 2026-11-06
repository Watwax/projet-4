package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

class AuthServiceTest {
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        this.authenticationManager = mock(AuthenticationManager.class);
        this.jwtUtils = mock(JwtUtils.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.userRepository = mock(UserRepository.class);
        this.authService = new AuthService(this.authenticationManager, this.passwordEncoder,
                this.jwtUtils, this.userRepository);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRegisterUserWithEncodedPassword() {
        SignupRequest request = new SignupRequest();
        request.setEmail("user@example.com");
        request.setLastName("Doe");
        request.setFirstName("Jane");
        request.setPassword("secret123");
        when(this.passwordEncoder.encode("secret123")).thenReturn("encoded-password");

        assertTrue(this.authService.registerUser(request));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(this.userRepository).save(savedUser.capture());
        assertEquals("encoded-password", savedUser.getValue().getPassword());
    }

    @Test
    void shouldNotSaveUserWhenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setEmail("user@example.com");
        when(this.userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertFalse(this.authService.registerUser(request));

        verify(this.userRepository, never()).save(any(User.class));
        verify(this.passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldAuthenticateAndIncludeAdminStatus() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.com");
        request.setPassword("secret123");
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("admin@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();
        Authentication authentication = mock(Authentication.class);
        when(this.authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(this.jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(this.userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(new User("admin@example.com", "Doe", "Jane", "encoded", true)));

        JwtResponse response = this.authService.authenticateUser(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("admin@example.com", response.getUsername());
        assertTrue(response.getAdmin());
    }

    @Test
    void shouldDefaultToRegularUserWhenEmailLookupReturnsNoUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("secret123");
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("user@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();
        Authentication authentication = mock(Authentication.class);
        when(this.authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(this.jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(this.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        JwtResponse response = this.authService.authenticateUser(request);

        assertFalse(response.getAdmin());
        assertEquals("jwt-token", response.getToken());
        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldEncodeAndSaveNewUserAsNonAdmin() {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@example.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPassword("secret123");
        when(this.userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(this.passwordEncoder.encode("secret123")).thenReturn("encoded-password");

        assertTrue(this.authService.registerUser(request));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(this.userRepository).save(savedUser.capture());
        assertEquals("new@example.com", savedUser.getValue().getEmail());
        assertEquals("Jane", savedUser.getValue().getFirstName());
        assertEquals("Doe", savedUser.getValue().getLastName());
        assertEquals("encoded-password", savedUser.getValue().getPassword());
        assertFalse(savedUser.getValue().isAdmin());
    }
}
