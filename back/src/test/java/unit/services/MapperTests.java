package unit.services;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class MapperTests {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);

    @Test
    public void testUserToUserDtoMapping() {
        // Given
        User user = new User(1L, "user@test.com", "LastName", "FirstName", "password", true, null, null);

        // When
        UserDto userDto = userMapper.toDto(user);

        // Then
        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.isAdmin(), userDto.isAdmin());
        // Add more assertions based on your mapping
    }

    @Test
    public void testUserDtoToUserMapping() {
        // Given
        UserDto userDto = new UserDto(1L, "user@test.com", "LastName", "FirstName", true, "password", null, null);

        // When
        User user = userMapper.toEntity(userDto);

        // Then
        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.isAdmin(), user.isAdmin());
    }

    @Test
    public void testTeacherToDto() {
        // Given
        Teacher teacher = new Teacher(1L, "Doe", "John", null, null);
        TeacherDto expectedDto = new TeacherDto(1L, "Doe", "John", null, null);

        // When
        TeacherDto actualDto = teacherMapper.toDto(teacher);

        // Then
        assertNotNull(actualDto);
        assertEquals(expectedDto.getId(), actualDto.getId());
        assertEquals(expectedDto.getLastName(), actualDto.getLastName());
        assertEquals(expectedDto.getFirstName(), actualDto.getFirstName());
        // Add more assertions based on your mapping
    }

    @Test
    public void testDtoToTeacher() {
        // Given
        TeacherDto teacherDto = new TeacherDto(1L, "Doe", "John", null, null);

        // When
        Teacher actualTeacher = teacherMapper.toEntity(teacherDto);

        // Then
        assertNotNull(actualTeacher);
        assertEquals(teacherDto.getId(), actualTeacher.getId());
        assertEquals(teacherDto.getLastName(), actualTeacher.getLastName());
        assertEquals(teacherDto.getFirstName(), actualTeacher.getFirstName());
        // Add more assertions based on your mapping
    }
}
