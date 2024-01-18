package unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;


@ExtendWith(MockitoExtension.class)
public class UserMapperTests {

	private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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

}
