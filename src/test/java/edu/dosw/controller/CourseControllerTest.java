package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.UpdateCourseDTO;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.services.FacultyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

  @Mock private FacultyService facultyService;

  @InjectMocks private CourseController courseController;

  @Test
  void getCourseById_WhenCourseExists_ShouldReturnCourse() {
    String courseAbbreviation = "MATH101";
    String facultyName = "Engineering";
    String plan = "2024";
    Course expectedCourse = new Course("MATH101", "Mathematics", 4);

    when(facultyService.findCourseByAbbreviation(courseAbbreviation, facultyName, plan))
        .thenReturn(expectedCourse);

    ResponseEntity<Course> response =
        courseController.getCourseById(courseAbbreviation, facultyName, plan);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(courseAbbreviation, response.getBody().getAbbreviation());
    verify(facultyService, times(1))
        .findCourseByAbbreviation(courseAbbreviation, facultyName, plan);
  }

  @Test
  void getCourseById_WhenCourseNotExists_ShouldThrowException() {
    String courseAbbreviation = "NONEXISTENT";
    String facultyName = "Engineering";
    String plan = "2024";

    when(facultyService.findCourseByAbbreviation(courseAbbreviation, facultyName, plan))
        .thenThrow(new RuntimeException("Course not found"));

    assertThrows(
        RuntimeException.class,
        () -> courseController.getCourseById(courseAbbreviation, facultyName, plan));
    verify(facultyService, times(1))
        .findCourseByAbbreviation(courseAbbreviation, facultyName, plan);
  }

  @Test
  void createCourse_WithValidData_ShouldReturnFaculty() {
    CourseRequest courseRequest =
        new CourseRequest("MATH101", "Mathematics", 4, "Engineering", "2024");
    Faculty expectedFaculty = new Faculty();

    when(facultyService.addCourse(courseRequest)).thenReturn(expectedFaculty);

    ResponseEntity<Faculty> response = courseController.createCourse(courseRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(facultyService, times(1)).addCourse(courseRequest);
  }

  @Test
  void createCourse_WithInvalidData_ShouldThrowException() {
    CourseRequest invalidRequest = new CourseRequest("", "", 0, "", "");

    when(facultyService.addCourse(invalidRequest))
        .thenThrow(new RuntimeException("Invalid course data"));

    assertThrows(RuntimeException.class, () -> courseController.createCourse(invalidRequest));
    verify(facultyService, times(1)).addCourse(invalidRequest);
  }

  @Test
  void createCourse_WithDuplicateCourse_ShouldThrowException() {
    CourseRequest duplicateRequest =
        new CourseRequest("MATH101", "Mathematics", 4, "Engineering", "2024");

    when(facultyService.addCourse(duplicateRequest))
        .thenThrow(new RuntimeException("Course already exists"));

    assertThrows(RuntimeException.class, () -> courseController.createCourse(duplicateRequest));
    verify(facultyService, times(1)).addCourse(duplicateRequest);
  }

  @Test
  void updateCourse_WithValidData_ShouldReturnUpdatedCourse() {
    String courseAbbreviation = "MATH101";
    String facultyName = "Engineering";
    String plan = "2024";
    UpdateCourseDTO updateDTO = new UpdateCourseDTO("Advanced Mathematics", 5);
    Course updatedCourse = new Course("MATH101", "Advanced Mathematics", 5);

    when(facultyService.updateCourse(courseAbbreviation, facultyName, plan, updateDTO))
        .thenReturn(updatedCourse);

    ResponseEntity<Course> response =
        courseController.updateCourse(courseAbbreviation, facultyName, plan, updateDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Advanced Mathematics", response.getBody().getCourseName());
    assertEquals(5, response.getBody().getCredits());
    verify(facultyService, times(1)).updateCourse(courseAbbreviation, facultyName, plan, updateDTO);
  }

  @Test
  void updateCourse_WhenCourseNotExists_ShouldThrowException() {
    String courseAbbreviation = "NONEXISTENT";
    String facultyName = "Engineering";
    String plan = "2024";
    UpdateCourseDTO updateDTO = new UpdateCourseDTO("New Name", 3);

    when(facultyService.updateCourse(courseAbbreviation, facultyName, plan, updateDTO))
        .thenThrow(new RuntimeException("Course not found"));

    assertThrows(
        RuntimeException.class,
        () -> courseController.updateCourse(courseAbbreviation, facultyName, plan, updateDTO));
    verify(facultyService, times(1)).updateCourse(courseAbbreviation, facultyName, plan, updateDTO);
  }

  @Test
  void deleteCourseByCourseAbbreviation_WhenCourseExists_ShouldReturnNoContent() {
    String courseAbbreviation = "MATH101";
    String facultyName = "Engineering";
    String plan = "2024";

    doNothing().when(facultyService).deleteCourse(courseAbbreviation, facultyName, plan);

    ResponseEntity<Void> response =
        courseController.deleteCourseByCourseAbbreviation(courseAbbreviation, facultyName, plan);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(facultyService, times(1)).deleteCourse(courseAbbreviation, facultyName, plan);
  }

  @Test
  void deleteCourseByCourseAbbreviation_WhenCourseNotExists_ShouldThrowException() {
    String courseAbbreviation = "NONEXISTENT";
    String facultyName = "Engineering";
    String plan = "2024";

    doThrow(new RuntimeException("Course not found"))
        .when(facultyService)
        .deleteCourse(courseAbbreviation, facultyName, plan);

    assertThrows(
        RuntimeException.class,
        () ->
            courseController.deleteCourseByCourseAbbreviation(
                courseAbbreviation, facultyName, plan));
    verify(facultyService, times(1)).deleteCourse(courseAbbreviation, facultyName, plan);
  }

  @Test
  void updateCourse_WithPartialData_ShouldReturnUpdatedCourse() {
    String courseAbbreviation = "MATH101";
    String facultyName = "Engineering";
    String plan = "2024";
    UpdateCourseDTO updateDTO = new UpdateCourseDTO(null, 5);
    Course updatedCourse = new Course("MATH101", "Mathematics", 5);

    when(facultyService.updateCourse(courseAbbreviation, facultyName, plan, updateDTO))
        .thenReturn(updatedCourse);

    ResponseEntity<Course> response =
        courseController.updateCourse(courseAbbreviation, facultyName, plan, updateDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(5, response.getBody().getCredits());
    verify(facultyService, times(1)).updateCourse(courseAbbreviation, facultyName, plan, updateDTO);
  }

  @Test
  void updateCourse_WithOnlyCourseName_ShouldReturnUpdatedCourse() {
    String courseAbbreviation = "MATH101";
    String facultyName = "Engineering";
    String plan = "2024";
    UpdateCourseDTO updateDTO = new UpdateCourseDTO("Advanced Mathematics", null);
    Course updatedCourse = new Course("MATH101", "Advanced Mathematics", 4);

    when(facultyService.updateCourse(courseAbbreviation, facultyName, plan, updateDTO))
        .thenReturn(updatedCourse);

    ResponseEntity<Course> response =
        courseController.updateCourse(courseAbbreviation, facultyName, plan, updateDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Advanced Mathematics", response.getBody().getCourseName());
    verify(facultyService, times(1)).updateCourse(courseAbbreviation, facultyName, plan, updateDTO);
  }
}
