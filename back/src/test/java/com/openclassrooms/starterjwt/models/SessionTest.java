package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {
    @Test
    void shouldCompareSessionsByIdAndHandleNullIds() {
        Session session = Session.builder().id(1L).build();
        Session sameId = Session.builder().id(1L).build();
        Session differentId = Session.builder().id(2L).build();
        Session withoutId = new Session();
        Session anotherWithoutId = new Session();

        assertEquals(session, session);
        assertEquals(session, sameId);
        assertEquals(session.hashCode(), sameId.hashCode());
        assertNotEquals(session, differentId);
        assertNotEquals(session, null);
        assertNotEquals(session, "session");
        assertEquals(withoutId, anotherWithoutId);
        assertEquals(withoutId.hashCode(), anotherWithoutId.hashCode());
        assertNotEquals(withoutId, session);
    }

    @Test
    void shouldIncludeSessionFieldsAndRelationsInStringRepresentation() {
        Teacher teacher = Teacher.builder().id(4L).firstName("Taylor").build();
        User user = User.builder()
                .id(7L)
                .email("member@example.test")
                .firstName("Taylor")
                .lastName("Smith")
                .password("test-password")
                .build();
        Session session = Session.builder()
                .id(1L)
                .name("Morning yoga")
                .date(new Date(0))
                .description("A session")
                .teacher(teacher)
                .users(List.of(user))
                .build();

        String representation = session.toString();

        assertTrue(representation.contains("name=Morning yoga"));
        assertTrue(representation.contains("teacher=" + teacher));
        assertTrue(representation.contains("users=[" + user + "]"));
        assertTrue(new Session().toString().contains("name=null"));
    }
}
