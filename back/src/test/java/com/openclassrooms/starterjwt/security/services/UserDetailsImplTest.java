package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsImplTest {
    @Test
    void shouldExposeUserDetailsAndDefaultAccountStatus() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("member@example.test")
                .firstName("Alex")
                .lastName("Member")
                .admin(false)
                .password("encoded-password")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("member@example.test", user.getUsername());
        assertEquals("Alex", user.getFirstName());
        assertEquals("Member", user.getLastName());
        assertFalse(user.getAdmin());
        assertEquals("encoded-password", user.getPassword());
        assertTrue(user.getAuthorities().isEmpty());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void shouldCompareUsersByIdentityAndId() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl sameId = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl differentId = UserDetailsImpl.builder().id(2L).build();

        assertTrue(user.equals(user));
        assertTrue(user.equals(sameId));
        assertFalse(user.equals(null));
        assertNotEquals(user, differentId);
        assertNotEquals(user, "different type");
    }
}
