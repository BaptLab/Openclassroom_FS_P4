package unit.services;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.services.UserService;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTests {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@InjectMocks
	private SessionService sessionService;

	@Test
	public void testCreateSession() {
		Session mockSession = new Session(1L, "Fake Session", new Date(),
				"This is a fake session for testing purposes.",
				new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now());
		when(sessionRepository.save(mockSession)).thenReturn(mockSession);

		Session resultSession = sessionService.create(mockSession);

		assertEquals(resultSession, mockSession);
		verify(sessionRepository, times(1)).save(mockSession);
	}

	@Test
	public void testDeleteSession() {
		Long sessionId = 1L;

		sessionService.delete(sessionId);

		verify(sessionRepository, times(1)).deleteById(sessionId);
	}

	@Test
	public void testFindAllSession() {
		sessionService.findAll();
		verify(sessionRepository, times(1)).findAll();
	}

	@Test
	public void testGetByIdSession_Found() {
		Long sessionId = 1L;
		Session mockSession = new Session(1L, "Fake Session", new Date(),
				"This is a fake session for testing purposes.",
				new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now());

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));

		Session result = sessionService.getById(sessionId);

		assertEquals(mockSession, result);
		verify(sessionRepository, times(1)).findById(sessionId);
	}

	@Test
	public void testGetByIdSession_Notfound() {
		Long sessionId = 1L;
		when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

		Session sessionResult = sessionService.getById(sessionId);

		assertNull(sessionResult);
		verify(sessionRepository, times(1)).findById(sessionId);
	}

	@Test
	public void testUpdateSession() {
		Long sessionId = 1L;
		Session mockSession = new Session(1L, "Fake Session", new Date(),
				"This is a fake session for testing purposes.",
				new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now());

		when(sessionRepository.save(mockSession)).thenReturn(mockSession);

		Session sessionResult = sessionService.update(sessionId, mockSession);

		assertEquals(mockSession, sessionResult);
		verify(sessionRepository, times(1)).save(mockSession);
	}

	@Test
	public void testParticipateSession() {
		Long sessionId = 1L;
		Long userId = 1L;

		Session mockSession = new Session(1L, "Fake Session", new Date(),
				"This is a fake session for testing purposes.",
				new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now());

		User mockUser = new User(userId, "mockuser@example.com", "Mock", "User", "mockpassword", false,
				LocalDateTime.now(), LocalDateTime.now());
		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		sessionService.participate(sessionId, userId);
		verify(sessionRepository, times(1)).save(mockSession);
	}

	@Test
	public void testParticipateSession_userNotFound() {
		Long sessionId = 1L;
		Long userId = 1L;

		Session mockSession = new Session(1L, "Fake Session", new Date(),
				"This is a fake session for testing purposes.",
				new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), new ArrayList<>(),
				LocalDateTime.now(), LocalDateTime.now());

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> {
			sessionService.participate(sessionId, userId);
		});
	}

	@Test
	public void testParticipateSession_sessionNotFound() {
		Long sessionId = 1L;
		Long userId = 1L;

		User mockUser = new User(userId, "mockuser@example.com", "Mock", "User", "mockpassword", false,
				LocalDateTime.now(), LocalDateTime.now());

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		assertThrows(NotFoundException.class, () -> {
			sessionService.participate(sessionId, userId);
		});
	}

	@Test
	public void testParticipateSession_alreadyParticipate() {
		Long sessionId = 1L;
		Long userId = 1L;

		Session mockSession = Mockito.mock(Session.class);
		User mockUser = Mockito.mock(User.class);

		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		// Make sure getUsers() returns a non-null list
		when(mockSession.getUsers()).thenReturn(Collections.singletonList(mockUser));

		// Stubbing the getId() method to return the expected userId
		when(mockUser.getId()).thenReturn(userId);

		assertThrows(BadRequestException.class, () -> {
			sessionService.participate(sessionId, userId);
		});
	}

	@Test
	public void testNoLongerParticipate() {
		Long sessionId = 1L;
		Long userId = 1L;

		List<User> mockUsers = new ArrayList<>();
		User mockUser = new User(userId, "mockuser@example.com", "Mock", "User", "mockpassword", false,
				LocalDateTime.now(), LocalDateTime.now());

		mockUsers.add(mockUser);

		Session mockSession = new Session(1L, "Fake Session", new Date(),
				"This is a fake session for testing purposes.",
				new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), mockUsers,
				LocalDateTime.now(), LocalDateTime.now());
		when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
		
		Assertions.assertTrue(mockSession.getUsers().stream().anyMatch(user -> user.getId().equals(userId)));
		
		sessionService.noLongerParticipate(sessionId, userId);
		
		verify(sessionRepository, times(1)).findById(sessionId);
		
		Assertions.assertFalse(mockSession.getUsers().stream().anyMatch(user -> user.getId().equals(userId)));
		verify(sessionRepository, times(1)).save(mockSession);
	}
	
	@Test
	public void testNoLongerParticipate_WrongUserId() {
	    Long sessionId = 1L;
	    Long userId = 1L;
	    Long wrongUserId = 2L;

	    List<User> mockUsers = new ArrayList<>();
	    User mockUser = new User(userId, "mockuser@example.com", "Mock", "User", "mockpassword", false,
	            LocalDateTime.now(), LocalDateTime.now());

	    mockUsers.add(mockUser);

	    Session mockSession = new Session(1L, "Fake Session", new Date(),
	            "This is a fake session for testing purposes.",
	            new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), mockUsers,
	            LocalDateTime.now(), LocalDateTime.now());

	    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));

	    Assertions.assertTrue(mockSession.getUsers().stream().anyMatch(user -> user.getId().equals(userId)));

	    // We expect a BadRequestException when calling the method with the wrong Id
	    Assertions.assertThrows(BadRequestException.class, () -> {
	        sessionService.noLongerParticipate(sessionId, wrongUserId);
	    });

	    // Verify that findById is called on the sessionRepository
	    verify(sessionRepository, times(1)).findById(sessionId);

	    // Verify that the session state remains unchanged
	    Assertions.assertTrue(mockSession.getUsers().stream().anyMatch(user -> user.getId().equals(userId)));
	    
	    // Verify that save is not called on the sessionRepository
	    verify(sessionRepository, times(0)).save(mockSession);
	}

	@Test
	public void testNoLongerParticipate_SessionNotFound() {
	    Long sessionId = 1L;
	    Long userId = 1L;

	    List<User> mockUsers = new ArrayList<>();
	    User mockUser = new User(userId, "mockuser@example.com", "Mock", "User", "mockpassword", false,
	            LocalDateTime.now(), LocalDateTime.now());

	    mockUsers.add(mockUser);

	    Session mockSession = new Session(1L, "Fake Session", new Date(),
	            "This is a fake session for testing purposes.",
	            new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), mockUsers,
	            LocalDateTime.now(), LocalDateTime.now());

	    when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

	    Assertions.assertThrows(NotFoundException.class, () -> {
	        sessionService.noLongerParticipate(sessionId, userId);
	    });

	    verify(sessionRepository, times(1)).findById(sessionId);
	    verify(sessionRepository, times(0)).save(mockSession);
	}
	
	@Test
	public void testNoLongerParticipate_UserNotParticipating() {
	    Long sessionId = 1L;
	    Long userId = 1L;

	    List<User> mockUsers = new ArrayList<>();
	    User mockUser = new User(userId, "mockuser@example.com", "Mock", "User", "mockpassword", false,
	            LocalDateTime.now(), LocalDateTime.now());

	    mockUsers.add(mockUser);

	    Session mockSession = new Session(1L, "Fake Session", new Date(),
	            "This is a fake session for testing purposes.",
	            new Teacher(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()), mockUsers,
	            LocalDateTime.now(), LocalDateTime.now());

	    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
	    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));

	    Assertions.assertTrue(mockSession.getUsers().stream().anyMatch(user -> user.getId().equals(userId)));

	    sessionService.noLongerParticipate(sessionId, userId);

	    verify(sessionRepository, times(1)).findById(sessionId);
	    Assertions.assertFalse(mockSession.getUsers().stream().anyMatch(user -> user.getId().equals(userId)));
	    verify(sessionRepository, times(1)).save(mockSession);
	}

}
