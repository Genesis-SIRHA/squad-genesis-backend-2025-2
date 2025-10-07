package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.services.FacultyService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class CourseControllerTest {

  @Mock private FacultyService facultyService;

  @InjectMocks private CourseController courseController;

  private Faculty faculty;
  private Course course;
  private CourseRequest courseRequest;
  private GroupRequest groupRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    course = new Course();
    course.setAbbreviation("CS101");
    course.setCourseName("Software Engineering");
    course.setCredits(4);

    faculty = new Faculty("Engineering", "2025", new ArrayList<>(List.of(course)));

    groupRequest = new GroupRequest("G1", "CS101", "2025", "1", "T001", true, 1, 30, 25);

    courseRequest = new CourseRequest("CS101", "Software Engineering", 4, "Engineering", "2025");
  }

  @Test
  void shouldReturnCourseByIdWhenExists() {
    when(facultyService.findCourseByCode("CS101")).thenReturn(Optional.of(course));

    ResponseEntity<Course> response = courseController.getCourseById("CS101");

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals("CS101", response.getBody().getAbbreviation());
    verify(facultyService).findCourseByCode("CS101");
  }

  @Test
  void shouldReturnNotFoundWhenCourseDoesNotExist() {
    when(facultyService.findCourseByCode("INVALID")).thenReturn(Optional.empty());

    ResponseEntity<Course> response = courseController.getCourseById("INVALID");

    assertEquals(404, response.getStatusCodeValue());
    assertNull(response.getBody());
    verify(facultyService).findCourseByCode("INVALID");
  }

  @Test
  void shouldCreateCourse() {
    when(facultyService.createCourse(courseRequest)).thenReturn(faculty);

    ResponseEntity<Faculty> response = courseController.createCourse(courseRequest);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(faculty, response.getBody());
    verify(facultyService).createCourse(courseRequest);
  }

  @Test
  void shouldUpdateCourseWhenExists() {
    when(facultyService.updateCourse("CS101", courseRequest)).thenReturn(course);

    ResponseEntity<Course> response = courseController.updateCourse("CS101", courseRequest);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(faculty, response.getBody());
    verify(facultyService).updateCourse("CS101", courseRequest);
  }

  @Test
  void shouldAddGroupToCourseSuccessfully() {
    when(facultyService.addGroupToCourse(groupRequest)).thenReturn(true);

    ResponseEntity<Course> response = courseController.addGroupToCourse("CS101", groupRequest);

    assertEquals(200, response.getStatusCodeValue());
    verify(facultyService).addGroupToCourse(groupRequest);
  }

  @Test
  void shouldReturnBadRequestWhenAddGroupFails() {
    when(facultyService.addGroupToCourse(groupRequest)).thenReturn(false);

    ResponseEntity<Course> response = courseController.addGroupToCourse("CS101", groupRequest);

    assertEquals(400, response.getStatusCodeValue());
    verify(facultyService).addGroupToCourse(groupRequest);
  }

  @Test
  void shouldDeleteCourse() {
    ResponseEntity<Void> response = courseController.deleteCourse("CS101");

    assertEquals(204, response.getStatusCodeValue());
    verify(facultyService).deleteCourse("CS101");
  }
}
