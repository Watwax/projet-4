package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapperIntegrationTest extends ApiIntegrationTestSupport {

    @Test
    void shouldMapTeachersAndHandleNullMapperInputs() {
        TeacherDto teacherDto = this.teacherMapper.toDto(this.teacher);

        assertEquals(this.teacher.getId(), teacherDto.getId());
        assertEquals(this.teacher.getFirstName(), teacherDto.getFirstName());
        assertEquals(this.teacher.getLastName(), teacherDto.getLastName());
        assertNotNull(this.teacherMapper.toEntity(teacherDto));
        assertNull(this.teacherMapper.toDto((Teacher) null));
        assertNull(this.teacherMapper.toEntity((TeacherDto) null));
        assertEquals(2, this.teacherMapper.toDto(Arrays.asList(this.teacher, null)).size());
        assertEquals(2, this.teacherMapper.toEntity(Arrays.asList(teacherDto, null)).size());
        assertNull(this.teacherMapper.toDto((java.util.List<Teacher>) null));
        assertNull(this.teacherMapper.toEntity((java.util.List<TeacherDto>) null));
    }

    @Test
    void shouldMapUsersAndHandleNullMapperInputs() {
        UserDto userDto = this.userMapper.toDto(this.member);

        assertEquals(this.member.getId(), userDto.getId());
        assertEquals(this.member.getEmail(), userDto.getEmail());
        assertEquals(this.member.getFirstName(), userDto.getFirstName());
        assertEquals(this.member.getLastName(), userDto.getLastName());
        assertNotNull(this.userMapper.toEntity(userDto));
        assertNull(this.userMapper.toDto((User) null));
        assertNull(this.userMapper.toEntity((UserDto) null));
        assertEquals(2, this.userMapper.toDto(Arrays.asList(this.member, null)).size());
        assertEquals(2, this.userMapper.toEntity(Arrays.asList(userDto, null)).size());
        assertNull(this.userMapper.toDto((java.util.List<User>) null));
        assertNull(this.userMapper.toEntity((java.util.List<UserDto>) null));
    }

    @Test
    void shouldMapSessionRelationshipsAndHandleMissingParticipants() {
        SessionDto sessionDto = new SessionDto(
                null,
                "Mapped session",
                new Date(),
                this.teacher.getId(),
                "A mapped session",
                Arrays.asList(this.member.getId(), Long.MAX_VALUE),
                null,
                null);

        Session mapped = this.sessionMapper.toEntity(sessionDto);
        assertEquals(this.teacher.getId(), mapped.getTeacher().getId());
        assertEquals(this.member.getId(), mapped.getUsers().get(0).getId());
        assertNull(mapped.getUsers().get(1));

        SessionDto mappedDto = this.sessionMapper.toDto(this.saveSession("Mapped session"));
        assertEquals(this.teacher.getId(), mappedDto.getTeacher_id());
        assertEquals(0, mappedDto.getUsers().size());
    }

    @Test
    void shouldMapSessionsWithoutOptionalRelationships() {
        SessionDto sessionDto = new SessionDto(
                null,
                "Unassigned session",
                new Date(),
                null,
                "No optional relationships",
                null,
                null,
                null);

        Session mapped = this.sessionMapper.toEntity(sessionDto);
        assertNull(mapped.getTeacher());
        assertEquals(0, mapped.getUsers().size());

        Session entity = new Session()
                .setName("Unassigned session")
                .setDate(new Date())
                .setDescription("No optional relationships")
                .setTeacher(null)
                .setUsers(null);
        SessionDto mappedDto = this.sessionMapper.toDto(entity);
        assertNull(mappedDto.getTeacher_id());
        assertEquals(0, mappedDto.getUsers().size());
    }

    @Test
    void shouldMapSessionListsAndHandleNullMapperInputs() {
        Session session = this.saveSession("Listed session");
        SessionDto sessionDto = this.sessionMapper.toDto(session);

        assertNull(this.sessionMapper.toDto((Session) null));
        assertNull(this.sessionMapper.toEntity((SessionDto) null));
        assertEquals(2, this.sessionMapper.toDto(Arrays.asList(session, null)).size());
        assertEquals(2, this.sessionMapper.toEntity(Arrays.asList(sessionDto, null)).size());
        assertNull(this.sessionMapper.toDto((java.util.List<Session>) null));
        assertNull(this.sessionMapper.toEntity((java.util.List<SessionDto>) null));
    }
}
