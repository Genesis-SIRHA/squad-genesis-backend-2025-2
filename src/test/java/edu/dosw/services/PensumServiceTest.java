package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Historial;
import edu.dosw.model.Pemsum;
import edu.dosw.model.User;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PensumServiceTest {

  @Mock private FacultyService facultyService;

  @Mock private MembersService membersService;

  @Mock private HistorialService historialService;

  @InjectMocks private PemsumService pemsumService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldBuildPemsumSuccessfully() {
    // Arrange
    String studentId = "student123";
    User student = new User(studentId, "student", "John Doe", "PlanA", "Engineering");

    Course math = new Course("Math", "MAT101", 3);
    Course physics = new Course("Physics", "PHY101", 4);

    Historial mathHist = new Historial();
    mathHist.setGroupCode("MAT101");
    mathHist.setStatus("approved");

    Historial physicsHist = new Historial();
    physicsHist.setGroupCode("PHY101");
    physicsHist.setStatus("pending");

    when(membersService.listById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "PlanA"))
        .thenReturn(List.of(math, physics));
    when(historialService.getSessionsByCourses(eq(studentId), anyList()))
        .thenReturn(List.of(mathHist, physicsHist));

    // Act
    Pemsum result = pemsumService.getPemsum(studentId);

    // Assert
    assertNotNull(result);
    assertEquals(studentId, result.getStudentId());
    assertEquals("John Doe", result.getStudentName());
    assertEquals("Engineering", result.getFacultyName());
    assertEquals("PlanA", result.getFacultyPlan());
    assertEquals(7, result.getTotalCredits());
    assertEquals(0, result.getApprovedCredits());

    Map<Course, String> coursesMap = result.getCourses();
    assertEquals("pending", coursesMap.get(math));
    assertEquals("pending", coursesMap.get(physics));

    verify(membersService).listById(studentId);
    verify(facultyService).findCoursesByFacultyNameAndPlan("Engineering", "PlanA");
    verify(historialService).getSessionsByCourses(studentId, List.of(math, physics));
  }

  @Test
  void shouldThrowBusinessExceptionWhenNoCoursesFound() {
    // Arrange
    String studentId = "student123";
    User student = new User(studentId, "student", "John Doe", "InvalidPlan", "Engineering");

    when(membersService.listById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "InvalidPlan"))
        .thenReturn(List.of());

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> pemsumService.getPemsum(studentId));

    assertTrue(exception.getMessage().contains("Invalid faculty name or plan"));
    verify(membersService).listById(studentId);
    verify(facultyService).findCoursesByFacultyNameAndPlan("Engineering", "InvalidPlan");
    verifyNoInteractions(historialService);
  }

  @Test
  void shouldCalculateApprovedCreditsCorrectly() {
    // Arrange
    String studentId = "student123";
    User student = new User(studentId, "student", "Jane Doe", "PlanB", "Engineering");

    Course math = new Course("Math", "MAT101", 3);
    Course physics = new Course("Physics", "PHY101", 4);

    Historial mathHist = new Historial();
    mathHist.setGroupCode("MAT101");
    mathHist.setStatus("approved");

    when(membersService.listById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "PlanB"))
        .thenReturn(List.of(math, physics));
    when(historialService.getSessionsByCourses(eq(studentId), anyList()))
        .thenReturn(List.of(mathHist));

    // Act
    Pemsum result = pemsumService.getPemsum(studentId);

    // Assert
    assertEquals(7, result.getTotalCredits());
    assertEquals(0, result.getApprovedCredits());
    assertEquals("pending", result.getCourses().get(math));
    assertEquals("pending", result.getCourses().get(physics));
  }
}
