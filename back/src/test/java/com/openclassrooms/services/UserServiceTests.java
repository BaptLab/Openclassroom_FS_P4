package com.openclassrooms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	public void testDeleteUser() {
		Long userId = 1L;

		userService.delete(userId);

		verify(userRepository, times(1)).deleteById(userId);
	}

	@Test
	public void testFindByIdUser_Found() {
		Long userId = 1L;
		
		User mockUser = new User(
		        1L,
		        "mockuser@example.com",
		        "Mock",
		        "User",
		        "mockpassword",
		        false,
		        LocalDateTime.now(),
		        LocalDateTime.now()
		);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		
		User userResult = userService.findById(userId);
		
		assertEquals(mockUser, userResult);
		verify(userRepository, times(1)).findById(userId);
	}
	
	@Test
	public void testFindByIdUser_NotFound() {
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		
		User userResult = userService.findById(userId);
		
		assertNull(userResult);
		
		verify(userRepository, times(1)).findById(userId);
	}

}
