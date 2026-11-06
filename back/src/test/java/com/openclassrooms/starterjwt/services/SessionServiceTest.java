package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionServiceTest {
    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        this.sessionRepository = mock(SessionRepository.class);
        this.userRepository = mock(UserRepository.class);
        this.sessionService = new SessionService(this.sessionRepository, this.userRepository);
    }

    @Test
    void shouldCreateAndReturnSession() {
        Session session = new Session();
        when(this.sessionRepository.save(session)).thenReturn(session);

        assertSame(session, this.sessionService.create(session));
    }

    @Test
    void shouldReturnAllSessions() {
        List<Session> sessions = List.of(new Session(), new Session());
        when(this.sessionRepository.findAll()).thenReturn(sessions);

        assertSame(sessions, this.sessionService.findAll());
    }

    @Test
    void shouldReturnSessionWhenItExists() {
        Session session = new Session();
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertSame(session, this.sessionService.getById(1L));
    }

    @Test
    void shouldUpdateSessionUsingRequestedId() {
        Session session = new Session();
        when(this.sessionRepository.save(session)).thenReturn(session);

        Session result = this.sessionService.update(4L, session);

        assertSame(session, result);
        assertEquals(4L, session.getId());
        verify(this.sessionRepository).save(session);
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

    @Test
    void shouldAddUserToSessionWhenTheyAreNotParticipating() {
        Session session = Session.builder().users(new ArrayList<>()).build();
        User user = new User("user@example.com", "Doe", "Jane", "encoded", false).setId(2L);
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(this.userRepository.findById(2L)).thenReturn(Optional.of(user));

        this.sessionService.participate(1L, 2L);

        assertEquals(List.of(user), session.getUsers());
        verify(this.sessionRepository).save(session);
    }

    @Test
    void shouldRejectDuplicateParticipation() {
        User user = new User("user@example.com", "Doe", "Jane", "encoded", false).setId(2L);
        Session session = Session.builder().users(new ArrayList<>(List.of(user))).build();
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(this.userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> this.sessionService.participate(1L, 2L));

        verify(this.sessionRepository, never()).save(session);
    }

    @Test
    void shouldRejectParticipationForMissingUser() {
        Session session = Session.builder().users(new ArrayList<>()).build();
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(this.userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.sessionService.participate(1L, 2L));

        verify(this.sessionRepository, never()).save(session);
    }

    @Test
    void shouldRemoveParticipatingUserFromSession() {
        User participatingUser = new User("user@example.com", "Doe", "Jane", "encoded", false).setId(2L);
        User otherUser = new User("other@example.com", "Smith", "John", "encoded", false).setId(3L);
        Session session = Session.builder().users(new ArrayList<>(List.of(participatingUser, otherUser))).build();
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        this.sessionService.noLongerParticipate(1L, 2L);

        assertEquals(List.of(otherUser), session.getUsers());
        verify(this.sessionRepository).save(session);
    }

    @Test
    void shouldRejectRemovingUserWhoIsNotParticipating() {
        Session session = Session.builder().users(new ArrayList<>()).build();
        when(this.sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> this.sessionService.noLongerParticipate(1L, 2L));

        verify(this.sessionRepository, never()).save(session);
    }
}
