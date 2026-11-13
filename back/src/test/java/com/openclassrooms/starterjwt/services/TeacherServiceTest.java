package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeacherServiceTest {
    private TeacherRepository teacherRepository;
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        this.teacherRepository = mock(TeacherRepository.class);
        this.teacherService = new TeacherService(this.teacherRepository);
    }

    @Test
    void shouldReturnAllTeachers() {
        List<Teacher> teachers = List.of(new Teacher(), new Teacher());
        when(this.teacherRepository.findAll()).thenReturn(teachers);

        assertSame(teachers, this.teacherService.findAll());
    }

    @Test
    void shouldFindTeacherById() {
        Teacher teacher = new Teacher();
        when(this.teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        assertSame(teacher, this.teacherService.findById(1L));
        assertSame(teacher, this.teacherService.getById(1L));
    }

    @Test
    void shouldReturnNullWhenFindByIdDoesNotFindTeacher() {
        when(this.teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertNull(this.teacherService.findById(1L));
    }

    @Test
    void shouldThrowNotFoundWhenGetByIdDoesNotFindTeacher() {
        when(this.teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.teacherService.getById(1L));
    }
}
