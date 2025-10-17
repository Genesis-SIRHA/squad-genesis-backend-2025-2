package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.FacultyDto;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.services.FacultyService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

  @Mock private FacultyService facultyService;

  @InjectMocks private FacultyController facultyController;

  @Test
  void createFaculty_WithValidData_ShouldReturnFaculty() {
    FacultyDto facultyDto = new FacultyDto("Engineering", "2024", Arrays.asList());
    Faculty expectedFaculty = new Faculty();
    expectedFaculty.setFacultyName("Engineering");
    expectedFaculty.setPlan("2024");

    when(facultyService.createFaculty(facultyDto)).thenReturn(expectedFaculty);

    ResponseEntity<Faculty> response = facultyController.createFaculty(facultyDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Engineering", response.getBody().getFacultyName());
    verify(facultyService, times(1)).createFaculty(facultyDto);
  }

  @Test
  void createFaculty_WithInvalidData_ShouldThrowException() {
    FacultyDto invalidDto = new FacultyDto(null, null, null);
    when(facultyService.createFaculty(invalidDto))
        .thenThrow(new RuntimeException("Invalid faculty data"));

    assertThrows(RuntimeException.class, () -> facultyController.createFaculty(invalidDto));
    verify(facultyService, times(1)).createFaculty(invalidDto);
  }

  @Test
  void getFacultyByNameAndPlan_WhenFacultyExists_ShouldReturnFaculty() {
    String facultyName = "Engineering";
    String plan = "2024";
    Faculty expectedFaculty = new Faculty();
    expectedFaculty.setFacultyName(facultyName);
    expectedFaculty.setPlan(plan);

    when(facultyService.getFacultyByNameAndPlan(facultyName, plan)).thenReturn(expectedFaculty);

    ResponseEntity<Faculty> response = facultyController.getFacultyByNameAndPlan(facultyName, plan);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(facultyName, response.getBody().getFacultyName());
    assertEquals(plan, response.getBody().getPlan());
    verify(facultyService, times(1)).getFacultyByNameAndPlan(facultyName, plan);
  }

  @Test
  void getFacultyByNameAndPlan_WhenFacultyNotExists_ShouldThrowException() {
    String facultyName = "NonExistent";
    String plan = "2024";
    when(facultyService.getFacultyByNameAndPlan(facultyName, plan))
        .thenThrow(new RuntimeException("Faculty not found"));

    assertThrows(
        RuntimeException.class, () -> facultyController.getFacultyByNameAndPlan(facultyName, plan));
    verify(facultyService, times(1)).getFacultyByNameAndPlan(facultyName, plan);
  }

  @Test
  void getAllFaculties_WhenFacultiesExist_ShouldReturnFacultiesList() {
    Faculty faculty1 = new Faculty();
    faculty1.setFacultyName("Engineering");
    Faculty faculty2 = new Faculty();
    faculty2.setFacultyName("Science");
    List<Faculty> expectedFaculties = Arrays.asList(faculty1, faculty2);

    when(facultyService.getAllFaculties()).thenReturn(expectedFaculties);

    ResponseEntity<List<Faculty>> response = facultyController.getAllFaculties();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    verify(facultyService, times(1)).getAllFaculties();
  }

  @Test
  void getAllFaculties_WhenNoFaculties_ShouldReturnEmptyList() {
    when(facultyService.getAllFaculties()).thenReturn(Arrays.asList());

    ResponseEntity<List<Faculty>> response = facultyController.getAllFaculties();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(facultyService, times(1)).getAllFaculties();
  }

  @Test
  void updateFacultyByNameAndPlan_WithValidData_ShouldReturnUpdatedFaculty() {
    FacultyDto updateDto =
        new FacultyDto(
            "Engineering", "2024", Arrays.asList(new Course("MATH101", "Mathematics", 4)));
    Faculty updatedFaculty = new Faculty();
    updatedFaculty.setFacultyName("Engineering");
    updatedFaculty.setPlan("2024");

    when(facultyService.updateFacultyByNameAndPlan(updateDto)).thenReturn(updatedFaculty);

    ResponseEntity<Faculty> response = facultyController.updateFacultyByNameAndPlan(updateDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Engineering", response.getBody().getFacultyName());
    verify(facultyService, times(1)).updateFacultyByNameAndPlan(updateDto);
  }

  @Test
  void updateFacultyByNameAndPlan_WhenFacultyNotExists_ShouldThrowException() {
    FacultyDto updateDto = new FacultyDto("NonExistent", "2024", Arrays.asList());
    when(facultyService.updateFacultyByNameAndPlan(updateDto))
        .thenThrow(new RuntimeException("Faculty not found"));

    assertThrows(
        RuntimeException.class, () -> facultyController.updateFacultyByNameAndPlan(updateDto));
    verify(facultyService, times(1)).updateFacultyByNameAndPlan(updateDto);
  }

  @Test
  void createFaculty_WithCourses_ShouldReturnFacultyWithCourses() {
    List<Course> courses =
        Arrays.asList(new Course("MATH101", "Mathematics", 4), new Course("PHY101", "Physics", 3));
    FacultyDto facultyDto = new FacultyDto("Engineering", "2024", courses);
    Faculty expectedFaculty = new Faculty();
    expectedFaculty.setFacultyName("Engineering");
    expectedFaculty.setPlan("2024");
    expectedFaculty.setCourses(courses);

    when(facultyService.createFaculty(facultyDto)).thenReturn(expectedFaculty);

    ResponseEntity<Faculty> response = facultyController.createFaculty(facultyDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().getCourses().size());
    verify(facultyService, times(1)).createFaculty(facultyDto);
  }

  @Test
  void updateFacultyByNameAndPlan_WithNewCourses_ShouldReturnUpdatedFaculty() {
    List<Course> newCourses = Arrays.asList(new Course("CS101", "Computer Science", 4));
    FacultyDto updateDto = new FacultyDto("Engineering", "2024", newCourses);
    Faculty updatedFaculty = new Faculty();
    updatedFaculty.setFacultyName("Engineering");
    updatedFaculty.setPlan("2024");
    updatedFaculty.setCourses(newCourses);

    when(facultyService.updateFacultyByNameAndPlan(updateDto)).thenReturn(updatedFaculty);

    ResponseEntity<Faculty> response = facultyController.updateFacultyByNameAndPlan(updateDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getCourses().size());
    verify(facultyService, times(1)).updateFacultyByNameAndPlan(updateDto);
  }

  @Test
  void getAllFaculties_WhenServiceThrowsException_ShouldThrowException() {
    when(facultyService.getAllFaculties()).thenThrow(new RuntimeException("Database error"));

    assertThrows(RuntimeException.class, () -> facultyController.getAllFaculties());
    verify(facultyService, times(1)).getAllFaculties();
  }
}
