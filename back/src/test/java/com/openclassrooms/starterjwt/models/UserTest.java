package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    @Test
    void shouldCompareUsersByIdAndHandleNullIds() {
        User user = user(1L);
        User sameId = user(1L);
        User differentId = user(2L);
        User withoutId = new User();
        User anotherWithoutId = new User();

        assertEquals(user, user);
        assertEquals(user, sameId);
        assertEquals(user.hashCode(), sameId.hashCode());
        assertNotEquals(user, differentId);
        assertNotEquals(user, null);
        assertNotEquals(user, "user");
        assertEquals(withoutId, anotherWithoutId);
        assertEquals(withoutId.hashCode(), anotherWithoutId.hashCode());
        assertNotEquals(withoutId, user);
    }

    @Test
    void shouldIncludeUserFieldsInStringRepresentation() {
        User user = User.builder()
                .id(1L)
                .email("member@example.test")
                .firstName("Taylor")
                .lastName("Smith")
                .password("test-password")
                .admin(true)
                .build();

        String representation = user.toString();

        assertTrue(representation.contains("email=member@example.test"));
        assertTrue(representation.contains("firstName=Taylor"));
        assertTrue(representation.contains("admin=true"));
        assertTrue(new User().toString().contains("email=null"));
    }

    private User user(Long id) {
        return User.builder()
                .id(id)
                .email("member@example.test")
                .firstName("Taylor")
                .lastName("Smith")
                .password("test-password")
                .build();
    }
}
