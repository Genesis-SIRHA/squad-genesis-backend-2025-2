package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Historial;
import edu.dosw.model.Pemsum;
import edu.dosw.model.Student;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.services.UserServices.StudentService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PemsumServiceTest {

  @Mock private FacultyService facultyService;

  @Mock private StudentService studentService;

  @Mock private HistorialService historialService;

  @InjectMocks private PemsumService pemsumService;

  @Test
  void getPemsum_ShouldCalculateApprovedCreditsCorrectly_WithFINISHEDStatus() {
    String studentId = "STU001";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");
    student.setFullName("John Doe");

    List<Course> courses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4),
            new Course("PHYS101", "Physics I", 3),
            new Course("CHEM101", "Chemistry I", 3));

    List<Historial> historials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED),
            createHistorial(studentId, "PHYS101", HistorialStatus.FINISHED),
            createHistorial(studentId, "CHEM101", HistorialStatus.ON_GOING));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(courses);
    when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(historials);

    Pemsum result = pemsumService.getPemsum(studentId);

    assertNotNull(result);
    assertEquals(7, result.getApprovedCredits());
    assertEquals(10, result.getTotalCredits());

    Map<Course, String> coursesMap = result.getCourses();
    assertEquals("FINISHED", coursesMap.get(courses.get(0)));
    assertEquals("FINISHED", coursesMap.get(courses.get(1)));
    assertEquals("ON_GOING", coursesMap.get(courses.get(2)));
  }

  @Test
  void getPemsum_ShouldNotCountNonFINISHEDStatus_AsApprovedCredits() {
    String studentId = "STU002";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Science");
    student.setPlan("2024");
    student.setFullName("Jane Smith");

    List<Course> courses =
        Arrays.asList(
            new Course("BIO101", "Biology I", 3), new Course("CHEM101", "Chemistry I", 3));

    List<Historial> historials =
        Arrays.asList(
            createHistorial(studentId, "BIO101", HistorialStatus.FAILED),
            createHistorial(studentId, "CHEM101", HistorialStatus.CANCELLED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024")).thenReturn(courses);
    when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(historials);

    Pemsum result = pemsumService.getPemsum(studentId);

    assertNotNull(result);
    assertEquals(0, result.getApprovedCredits());
    assertEquals(6, result.getTotalCredits());

    Map<Course, String> coursesMap = result.getCourses();
    assertEquals("FAILED", coursesMap.get(courses.get(0)));
    assertEquals("CANCELLED", coursesMap.get(courses.get(1)));
  }

  @Test
  void getPemsum_WithMixedStatuses_ShouldCalculateCorrectly() {
    String studentId = "STU003";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");
    student.setFullName("Bob Wilson");

    List<Course> courses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4),
            new Course("PHYS101", "Physics I", 3),
            new Course("CS101", "Programming", 3),
            new Course("ENG101", "English", 2));

    List<Historial> historials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED),
            createHistorial(studentId, "PHYS101", HistorialStatus.FAILED),
            createHistorial(studentId, "CS101", HistorialStatus.FINISHED),
            createHistorial(studentId, "ENG101", HistorialStatus.SWAPPED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(courses);
    when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(historials);

    Pemsum result = pemsumService.getPemsum(studentId);

    assertNotNull(result);
    assertEquals(7, result.getApprovedCredits());
    assertEquals(12, result.getTotalCredits());
  }

  @Test
  void getPemsum_WithAllFINISHEDCourses_ShouldReturnFullCredits() {
    String studentId = "STU004";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");
    student.setFullName("Alice Brown");

    List<Course> courses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4),
            new Course("PHYS101", "Physics I", 3),
            new Course("CHEM101", "Chemistry I", 3));

    List<Historial> historials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED),
            createHistorial(studentId, "PHYS101", HistorialStatus.FINISHED),
            createHistorial(studentId, "CHEM101", HistorialStatus.FINISHED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(courses);
    when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(historials);

    Pemsum result = pemsumService.getPemsum(studentId);

    assertNotNull(result);
    assertEquals(10, result.getApprovedCredits());
    assertEquals(10, result.getTotalCredits());
  }

  @Test
  void getPemsum_WithNoHistorial_ShouldSetAllToPendingAndZeroApprovedCredits() {
    String studentId = "STU005";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");
    student.setFullName("Charlie Davis");

    List<Course> courses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4), new Course("PHYS101", "Physics I", 3));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(courses);
    when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(List.of());

    Pemsum result = pemsumService.getPemsum(studentId);

    assertNotNull(result);
    assertEquals(0, result.getApprovedCredits());
    assertEquals(7, result.getTotalCredits());

    Map<Course, String> coursesMap = result.getCourses();
    assertEquals("pending", coursesMap.get(courses.get(0)));
    assertEquals("pending", coursesMap.get(courses.get(1)));
  }

  @Test
  void getCompletedCoursesPercentage_ShouldCalculateCorrectPercentage() {
    String studentId = "STU006";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");

    List<Course> allCourses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4),
            new Course("PHYS101", "Physics I", 3),
            new Course("CHEM101", "Chemistry I", 3),
            new Course("CS101", "Programming", 3));

    List<Historial> allHistorials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED),
            createHistorial(studentId, "PHYS101", HistorialStatus.FINISHED),
            createHistorial(studentId, "CHEM101", HistorialStatus.FAILED),
            createHistorial(studentId, "CS101", HistorialStatus.ON_GOING));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024"))
        .thenReturn(allCourses);
    when(historialService.getAllHistorial()).thenReturn(allHistorials);

    double percentage = pemsumService.getCompletedCoursesPercentage(studentId);

    assertEquals(53.846, percentage, 0.1);
  }

  @Test
  void getCompletedCoursesPercentage_WithNoFinishedCourses_ShouldReturnZero() {
    String studentId = "STU007";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Science");
    student.setPlan("2024");

    List<Course> allCourses =
        Arrays.asList(
            new Course("BIO101", "Biology I", 3), new Course("CHEM101", "Chemistry I", 3));

    List<Historial> allHistorials =
        Arrays.asList(
            createHistorial(studentId, "BIO101", HistorialStatus.FAILED),
            createHistorial(studentId, "CHEM101", HistorialStatus.CANCELLED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024")).thenReturn(allCourses);
    when(historialService.getAllHistorial()).thenReturn(allHistorials);

    double percentage = pemsumService.getCompletedCoursesPercentage(studentId);

    assertEquals(0.0, percentage, 0.0);
  }

  @Test
  void getCompletedCoursesPercentage_WithAllCoursesFinished_ShouldReturn100() {
    String studentId = "STU008";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");

    List<Course> allCourses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4), new Course("PHYS101", "Physics I", 3));

    List<Historial> allHistorials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED),
            createHistorial(studentId, "PHYS101", HistorialStatus.FINISHED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024"))
        .thenReturn(allCourses);
    when(historialService.getAllHistorial()).thenReturn(allHistorials);

    double percentage = pemsumService.getCompletedCoursesPercentage(studentId);

    assertEquals(100.0, percentage, 0.0);
  }

  @Test
  void getCompletedCoursesPercentage_WithNoHistorial_ShouldReturnZero() {
    String studentId = "STU009";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");

    List<Course> allCourses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4), new Course("PHYS101", "Physics I", 3));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024"))
        .thenReturn(allCourses);
    when(historialService.getAllHistorial()).thenReturn(List.of());

    double percentage = pemsumService.getCompletedCoursesPercentage(studentId);

    assertEquals(0.0, percentage, 0.0);
  }

  private Historial createHistorial(String studentId, String groupCode, HistorialStatus status) {
    return new Historial.HistorialBuilder()
        .studentId(studentId)
        .groupCode(groupCode)
        .status(status)
        .year("2024")
        .period("1")
        .build();
  }

  @Test
  void getStudentCoursesStatus_ShouldCombineHistorialAndPendingCourses() {
    String studentId = "STU001";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");

    List<Course> facultyCourses =
        Arrays.asList(
            new Course("MATH101", "Calculus I", 4),
            new Course("PHYS101", "Physics I", 3),
            new Course("CHEM101", "Chemistry I", 3),
            new Course("CS101", "Programming", 3));

    List<Historial> allHistorials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED),
            createHistorial(studentId, "PHYS101", HistorialStatus.ON_GOING),
            createHistorial(studentId, "CHEM101", HistorialStatus.FAILED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024"))
        .thenReturn(facultyCourses);
    when(historialService.getAllHistorial()).thenReturn(allHistorials);

    Map<String, String> result = pemsumService.getStudentCoursesStatus(studentId);

    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("FINISHED", result.get("MATH101"));
    assertEquals("ON_GOING", result.get("PHYS101"));
    assertEquals("FAILED", result.get("CHEM101"));
    assertEquals("PENDING", result.get("CS101"));
  }

  @Test
  void getStudentCoursesStatus_ShouldPrioritizeNewStatus_WhenCourseAlreadyExists() {
    String studentId = "STU002";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Science");
    student.setPlan("2024");

    List<Course> facultyCourses = Arrays.asList(new Course("BIO101", "Biology I", 3));

    List<Historial> allHistorials =
        Arrays.asList(
            createHistorial(studentId, "BIO101", HistorialStatus.FAILED),
            createHistorial(studentId, "BIO101", HistorialStatus.ON_GOING));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024"))
        .thenReturn(facultyCourses);
    when(historialService.getAllHistorial()).thenReturn(allHistorials);

    Map<String, String> result = pemsumService.getStudentCoursesStatus(studentId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("ON_GOING", result.get("BIO101"));
  }

  @Test
  void getStudentCoursesStatus_ShouldThrowBusinessException_WhenTryingToChangeFinishedCourse() {
    String studentId = "STU003";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");

    List<Course> facultyCourses = Arrays.asList(new Course("MATH101", "Calculus I", 4));

    List<Historial> allHistorials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED),
            createHistorial(studentId, "MATH101", HistorialStatus.FAILED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024"))
        .thenReturn(facultyCourses);
    when(historialService.getAllHistorial()).thenReturn(allHistorials);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> pemsumService.getStudentCoursesStatus(studentId));

    assertEquals("Cannot change status of finished course: MATH101", exception.getMessage());
  }

  @Test
  void getStudentCoursesStatus_ShouldAllowSameFinishedStatus_WhenCourseIsAlreadyFinished() {
    String studentId = "STU004";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Engineering");
    student.setPlan("2024");

    List<Course> facultyCourses = Arrays.asList(new Course("MATH101", "Calculus I", 4));

    List<Historial> allHistorials =
        Arrays.asList(
            createHistorial(studentId, "MATH101", HistorialStatus.ON_GOING),
            createHistorial(studentId, "MATH101", HistorialStatus.FINISHED));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024"))
        .thenReturn(facultyCourses);
    when(historialService.getAllHistorial()).thenReturn(allHistorials);

    Map<String, String> result = pemsumService.getStudentCoursesStatus(studentId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("FINISHED", result.get("MATH101"));
  }

  @Test
  void getStudentCoursesStatus_WithNoHistorial_ShouldReturnAllPending() {
    String studentId = "STU005";
    Student student = new Student();
    student.setUserId(studentId);
    student.setFacultyName("Science");
    student.setPlan("2024");

    List<Course> facultyCourses =
        Arrays.asList(
            new Course("BIO101", "Biology I", 3), new Course("CHEM101", "Chemistry I", 3));

    when(studentService.getStudentById(studentId)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024"))
        .thenReturn(facultyCourses);
    when(historialService.getAllHistorial()).thenReturn(List.of());

    Map<String, String> result = pemsumService.getStudentCoursesStatus(studentId);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("PENDING", result.get("BIO101"));
    assertEquals("PENDING", result.get("CHEM101"));
  }
}
