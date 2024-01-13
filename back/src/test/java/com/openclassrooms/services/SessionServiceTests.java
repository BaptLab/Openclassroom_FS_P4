package com.openclassrooms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTests {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

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
}
