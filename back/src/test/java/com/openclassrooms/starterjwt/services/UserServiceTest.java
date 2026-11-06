package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.userService = new UserService(this.userRepository);
    }

    @Test
    void shouldDeleteUserWhenRequestingUserOwnsAccount() {
        User user = new User("user@example.com", "Doe", "Jane", "password", false);
        when(this.userRepository.findById(1L)).thenReturn(Optional.of(user));

        this.userService.delete(1L, "user@example.com");

        verify(this.userRepository).delete(user);
    }

    @Test
    void shouldNotDeleteAnotherUsersAccount() {
        User user = new User("user@example.com", "Doe", "Jane", "password", false);
        when(this.userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> this.userService.delete(1L, "other@example.com"));

        verify(this.userRepository, never()).delete(user);
    }

    @Test
    void shouldNotDeleteMissingUser() {
        when(this.userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.delete(1L, "user@example.com"));

        verify(this.userRepository, never()).deleteById(1L);
    }

    @Test
    void shouldFindUserById() {
        User user = new User("user@example.com", "Doe", "Jane", "password", false);
        when(this.userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertSame(user, this.userService.findById(1L));
        assertSame(user, this.userService.getById(1L));
    }

    @Test
    void shouldReturnNullFromFindByIdWhenUserDoesNotExist() {
        when(this.userRepository.findById(1L)).thenReturn(Optional.empty());

        assertNull(this.userService.findById(1L));
    }
}
