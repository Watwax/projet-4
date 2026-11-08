package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeacherTest {
    @Test
    void shouldCompareTeachersByIdAndHandleNullIds() {
        Teacher teacher = Teacher.builder().id(1L).build();
        Teacher sameId = Teacher.builder().id(1L).build();
        Teacher differentId = Teacher.builder().id(2L).build();
        Teacher withoutId = new Teacher();
        Teacher anotherWithoutId = new Teacher();

        assertEquals(teacher, teacher);
        assertEquals(teacher, sameId);
        assertEquals(teacher.hashCode(), sameId.hashCode());
        assertNotEquals(teacher, differentId);
        assertNotEquals(teacher, null);
        assertNotEquals(teacher, "teacher");
        assertEquals(withoutId, anotherWithoutId);
        assertEquals(withoutId.hashCode(), anotherWithoutId.hashCode());
        assertNotEquals(withoutId, teacher);
    }

    @Test
    void shouldIncludeTeacherFieldsInStringRepresentation() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Taylor")
                .lastName("Smith")
                .build();

        String representation = teacher.toString();

        assertTrue(representation.contains("firstName=Taylor"));
        assertTrue(representation.contains("lastName=Smith"));
        assertTrue(new Teacher().toString().contains("firstName=null"));
    }
}
