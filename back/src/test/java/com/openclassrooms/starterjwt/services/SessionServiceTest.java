package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionServiceTest {
    private SessionRepository sessionRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        this.sessionRepository = mock(SessionRepository.class);
        this.sessionService = new SessionService(this.sessionRepository, mock(UserRepository.class));
    }

    @Test
    void shouldFindAndDeleteSessionInService() {
        Session session = new Session();
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        this.sessionService.delete(1L);

        verify(this.sessionRepository).delete(session);
    }

    @Test
    void shouldNotDeleteMissingSession() {
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.sessionService.delete(1L));

        verify(this.sessionRepository, never()).deleteById(1L);
    }
}
