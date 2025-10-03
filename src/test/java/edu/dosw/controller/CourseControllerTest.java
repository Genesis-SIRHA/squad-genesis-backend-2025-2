package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.services.FacultyService;
import edu.dosw.services.GroupService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    Course result = courseController.getCourseById("CS101");

    assertNotNull(result);
    assertEquals("CS101", result.getAbbreviation());
    verify(facultyService).findCourseByCode("CS101");
  }

  @Test
  void shouldThrowExceptionWhenCourseDoesNotExist() {
    when(facultyService.findCourseByCode("INVALID")).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> courseController.getCourseById("INVALID"));

    verify(facultyService).findCourseByCode("INVALID");
  }

  @Test
  void shouldCreateCourse() {
    when(facultyService.createCourse(courseRequest)).thenReturn(faculty);

    Faculty result = courseController.createCourse(courseRequest);

    assertEquals(faculty, result);
    verify(facultyService).createCourse(courseRequest);
  }

  @Test
  void shouldUpdateCourseWhenExists() {
    when(facultyService.updateCourse("CS101", courseRequest)).thenReturn(faculty);

    Faculty result = courseController.updateCourse("CS101", courseRequest);

    assertEquals(faculty, result);
    verify(facultyService).updateCourse("CS101", courseRequest);
  }

  @Test
  void shouldNotAddGroupToCourseNotExistent() {

    GroupService groupService = mock(GroupService.class);
    // Arrange
    GroupRequest groupRequest =
        new GroupRequest("G01", "CS101", "2025", "1", "T123", true, 1, 30, 0);

    Course mockCourse = new Course();
    when(facultyService.findCourseByCode("CS101")).thenReturn(Optional.of(mockCourse));

    Group mockGroup = new Group();
    when(groupService.createGroup(any(GroupRequest.class))).thenReturn(mockGroup);

    // Act
    Boolean result = facultyService.addGroupToCourse(groupRequest);

    // Assert
    assertFalse(result);
  }

  @Test
  void shouldDeleteCourse() {
    courseController.deleteCourse("CS101");

    verify(facultyService).deleteCourse("CS101");
  }
}
