package edu.dosw.controller;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Course;
import edu.dosw.services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseController.
 */
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private Course course;
    private CourseRequest courseRequest;
    private GroupRequest groupRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        course = new Course("CS101", "Software Engineering");
        groupRequest = new GroupRequest(
                "G1",
                "CS101",
                "2025",
                "1",
                "T001",
                true,
                1,
                30,
                25
        );
        courseRequest = new CourseRequest("CS101", "Software Engineering", List.of(groupRequest));
    }

    @Test
    void shouldReturnAllCourses() {
        when(courseService.getAllCourses()).thenReturn(List.of(course));

        ResponseEntity<List<Course>> response = courseController.getAllCourses();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("CS101", response.getBody().get(0).getAbbreviation());
        verify(courseService).getAllCourses();
    }

    @Test
    void shouldReturnCourseByIdWhenExists() {
        when(courseService.findByCode("CS101")).thenReturn(Optional.of(course));

        ResponseEntity<Course> response = courseController.getCourseById("CS101");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("CS101", response.getBody().getAbbreviation());
        verify(courseService).findByCode("CS101");
    }

    @Test
    void shouldReturnNotFoundWhenCourseDoesNotExist() {
        when(courseService.findByCode("INVALID")).thenReturn(Optional.empty());

        ResponseEntity<Course> response = courseController.getCourseById("INVALID");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(courseService).findByCode("INVALID");
    }

    @Test
    void shouldCreateCourse() {
        when(courseService.createCourse(courseRequest)).thenReturn(course);

        ResponseEntity<Course> response = courseController.createCourse(courseRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(course, response.getBody());
        verify(courseService).createCourse(courseRequest);
    }

    @Test
    void shouldUpdateCourseWhenExists() {
        when(courseService.updateCourse("CS101", courseRequest)).thenReturn(Optional.of(course));

        ResponseEntity<Course> response = courseController.updateCourse("CS101", courseRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(course, response.getBody());
        verify(courseService).updateCourse("CS101", courseRequest);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingCourse() {
        when(courseService.updateCourse("INVALID", courseRequest)).thenReturn(Optional.empty());

        ResponseEntity<Course> response = courseController.updateCourse("INVALID", courseRequest);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(courseService).updateCourse("INVALID", courseRequest);
    }

    @Test
    void shouldAddGroupToCourseSuccessfully() {
        when(courseService.addGroupToCourse("CS101", groupRequest)).thenReturn(true);

        ResponseEntity<Course> response = courseController.addGroupToCourse("CS101", groupRequest);

        assertEquals(200, response.getStatusCodeValue());
        verify(courseService).addGroupToCourse("CS101", groupRequest);
    }

    @Test
    void shouldReturnBadRequestWhenAddGroupFails() {
        when(courseService.addGroupToCourse("CS101", groupRequest)).thenReturn(false);

        ResponseEntity<Course> response = courseController.addGroupToCourse("CS101", groupRequest);

        assertEquals(400, response.getStatusCodeValue());
        verify(courseService).addGroupToCourse("CS101", groupRequest);
    }

    @Test
    void shouldDeleteCourse() {
        ResponseEntity<Void> response = courseController.deleteCourse("CS101");

        assertEquals(204, response.getStatusCodeValue());
        verify(courseService).deleteCourse("CS101");
    }
}
