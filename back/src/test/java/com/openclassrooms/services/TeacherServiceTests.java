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

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTests {
	@Mock
	private TeacherRepository teacherRepository;

	@InjectMocks
	private TeacherService teacherService;

	@Test
	public void testFindAllUser() {
		teacherService.findAll();
		verify(teacherRepository, times(1)).findAll();
	}
	
	@Test
	
	public void testFindByIdTeacher_Found() {
		Long teacherId = 1L;
		Teacher mockTeacher = new Teacher(
				1L, "DOE", "John",LocalDateTime.now(), LocalDateTime.now());
		when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(mockTeacher));
	
		Teacher teacherResult = teacherService.findById(teacherId);
		
		assertEquals(mockTeacher, teacherResult);
		verify(teacherRepository, times(1)).findById(teacherId);
	}
	
	@Test
	public void testFindByIdTeacher_NotFound() {
		Long teacherId = 1L;
		
		when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());
	
		Teacher teacherResult = teacherService.findById(teacherId);
		
		assertNull(teacherResult);
		verify(teacherRepository, times(1)).findById(teacherId);
	}
	
	
	
}
