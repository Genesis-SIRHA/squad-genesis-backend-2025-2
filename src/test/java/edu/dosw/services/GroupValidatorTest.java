package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.model.Student;
import edu.dosw.services.UserServices.StudentService;
import edu.dosw.services.Validators.GroupValidator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupValidatorTest {

  @Mock private PeriodService periodService;

  @Mock private StudentService studentService;

  @Mock private FacultyService facultyService;

  @InjectMocks private GroupValidator groupValidator;

  private Group testGroup;
  private Student testStudent;
  private Faculty testFaculty;
  private Course testCourse;

  @BeforeEach
  void setUp() {
    testGroup = new Group();
    testGroup.setGroupCode("GROUP001");
    testGroup.setAbbreviation("MATH101");
    testGroup.setYear("2024");
    testGroup.setPeriod("1");
    testGroup.setEnrolled(20);
    testGroup.setMaxCapacity(30);

    testStudent = new Student();
    testStudent.setUserId("STU001");
    testStudent.setFacultyName("Engineering");
    testStudent.setPlan("2024");

    testCourse = new Course();
    testCourse.setAbbreviation("MATH101");

    testFaculty = new Faculty("Engineering", "2024", List.of(testCourse));
  }

  @Test
  void validateAddStudentToGroup_WhenGroupIsFull_ShouldThrowBusinessException() {
    testGroup.setEnrolled(30);
    testGroup.setMaxCapacity(30);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));

    assertTrue(exception.getMessage().contains("The group is full"));
  }

  @Test
  void validateAddStudentToGroup_WhenYearDoesNotMatch_ShouldThrowBusinessException() {
    when(periodService.getYear()).thenReturn("2023");
    when(periodService.getPeriod()).thenReturn("1");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));

    assertTrue(exception.getMessage().contains("The historial period and year does not match"));
  }

  @Test
  void validateAddStudentToGroup_WhenPeriodDoesNotMatch_ShouldThrowBusinessException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("2");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));

    assertTrue(exception.getMessage().contains("The historial period and year does not match"));
  }

  @Test
  void validateAddStudentToGroup_WhenStudentNotFound_ShouldThrowResourceNotFoundException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(studentService.getStudentById("STU001"))
        .thenThrow(new ResourceNotFoundException("Student not found"));

    assertThrows(
        ResourceNotFoundException.class,
        () -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));
  }

  @Test
  void validateAddStudentToGroup_WhenFacultyNotFound_ShouldThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(studentService.getStudentById("STU001")).thenReturn(testStudent);
    when(facultyService.getFacultyByNameAndPlan("Engineering", "2024"))
        .thenThrow(new ResourceNotFoundException("Faculty not found"));

    assertThrows(
        ResourceNotFoundException.class,
        () -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));
  }

  @Test
  void validateAddStudentToGroup_WithValidData_ShouldNotThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(studentService.getStudentById("STU001")).thenReturn(testStudent);
    when(facultyService.getFacultyByNameAndPlan("Engineering", "2024")).thenReturn(testFaculty);

    assertDoesNotThrow(() -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));

    verify(periodService).getYear();
    verify(periodService).getPeriod();
    verify(studentService).getStudentById("STU001");
    verify(facultyService).getFacultyByNameAndPlan("Engineering", "2024");
  }

  @Test
  void validateAddStudentToGroup_WhenGroupHasAvailableSpace_ShouldNotThrowException() {
    testGroup.setEnrolled(25);
    testGroup.setMaxCapacity(30);

    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(studentService.getStudentById("STU001")).thenReturn(testStudent);
    when(facultyService.getFacultyByNameAndPlan("Engineering", "2024")).thenReturn(testFaculty);

    assertDoesNotThrow(() -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));
  }

  @Test
  void validateAddStudentToGroup_WhenCourseAbbreviationMatches_ShouldNotThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(studentService.getStudentById("STU001")).thenReturn(testStudent);
    when(facultyService.getFacultyByNameAndPlan("Engineering", "2024")).thenReturn(testFaculty);

    assertDoesNotThrow(() -> groupValidator.validateAddStudentToGroup(testGroup, "STU001"));
  }
}
