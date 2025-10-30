package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CourseStatus;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.*;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.services.UserServices.StudentService;
import java.util.ArrayList;
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

  @Mock private GroupService groupService;

  @InjectMocks private PemsumService pemsumService;

  private final String STUDENT_ID = "12345";
  private final String FACULTY_NAME = "Engineering";
  private final String PLAN = "2024";

  @Test
  void getPemsum_ShouldReturnPemsum() {
    Student student = createStudent();
    List<Course> courses = createCourses();
    List<Historial> historials = createHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getSessionsByCourses(STUDENT_ID, courses)).thenReturn(historials);

    Pemsum result = pemsumService.getPemsum(STUDENT_ID);

    assertNotNull(result);
    assertEquals(STUDENT_ID, result.getStudentId());
    assertEquals("John Doe", result.getStudentName());
    assertEquals(FACULTY_NAME, result.getFacultyName());
    assertEquals(PLAN, result.getFacultyPlan());
    assertEquals(9, result.getTotalCredits());
    assertEquals(3, result.getApprovedCredits());
    assertEquals(3, result.getCourses().size());
  }

  @Test
  void getPemsum_ShouldHandleCoursesWithRequisites() {
    Student student = createStudent();
    List<Course> courses = createCoursesWithRequisites();
    List<Historial> historials = createHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getSessionsByCourses(STUDENT_ID, courses)).thenReturn(historials);

    Pemsum result = pemsumService.getPemsum(STUDENT_ID);

    assertNotNull(result);
    assertEquals(12, result.getTotalCredits());
    assertEquals(3, result.getCourses().size());
  }

  @Test
  void getPemsum_ShouldHandleCoursesWithSemester() {
    Student student = createStudent();
    List<Course> courses = createCoursesWithSemester();
    List<Historial> historials = createHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getSessionsByCourses(STUDENT_ID, courses)).thenReturn(historials);

    Pemsum result = pemsumService.getPemsum(STUDENT_ID);

    assertNotNull(result);
    assertEquals(9, result.getTotalCredits());
    assertEquals(3, result.getCourses().size());
  }

  @Test
  void getPemsum_ShouldThrowException_WhenNoCoursesFound() {
    Student student = createStudent();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN))
        .thenReturn(new ArrayList<>());

    assertThrows(ResourceNotFoundException.class, () -> pemsumService.getPemsum(STUDENT_ID));
  }

  @Test
  void getCompletedCoursesPercentage_ShouldReturnPercentage() {
    Student student = createStudent();
    List<Course> courses = createCourses();
    List<Historial> finishedHistorials = createFinishedHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentIdAndStatus(STUDENT_ID, HistorialStatus.FINISHED))
        .thenReturn(finishedHistorials);
    when(groupService.getGroupByGroupCode("GRP001")).thenReturn(createGroup("MATH101"));
    when(groupService.getGroupByGroupCode("GRP002")).thenReturn(createGroup("PHYS101"));

    double result = pemsumService.getCompletedCoursesPercentage(STUDENT_ID);

    assertEquals(66.66, result, 0.01);
  }

  @Test
  void getCompletedCoursesPercentage_ShouldHandleCoursesWithDifferentCredits() {
    Student student = createStudent();
    List<Course> courses = createCoursesWithDifferentCredits();
    List<Historial> finishedHistorials = createFinishedHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentIdAndStatus(STUDENT_ID, HistorialStatus.FINISHED))
        .thenReturn(finishedHistorials);
    when(groupService.getGroupByGroupCode("GRP001")).thenReturn(createGroup("MATH101"));
    when(groupService.getGroupByGroupCode("GRP002")).thenReturn(createGroup("PHYS101"));

    double result = pemsumService.getCompletedCoursesPercentage(STUDENT_ID);

    assertEquals(50.0, result, 0.01);
  }

  @Test
  void getCompletedCoursesPercentage_ShouldThrowException_WhenNoCoursesFound() {
    Student student = createStudent();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN))
        .thenReturn(new ArrayList<>());

    assertThrows(
        ResourceNotFoundException.class,
        () -> pemsumService.getCompletedCoursesPercentage(STUDENT_ID));
  }

  @Test
  void getCompletedCoursesPercentage_ShouldReturnZero_WhenNoApprovedCourses() {
    Student student = createStudent();
    List<Course> courses = createCourses();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentIdAndStatus(STUDENT_ID, HistorialStatus.FINISHED))
        .thenReturn(new ArrayList<>());

    double result = pemsumService.getCompletedCoursesPercentage(STUDENT_ID);

    assertEquals(0.0, result);
  }

  @Test
  void getCompletedCoursesPercentage_ShouldHandleAllStatusTypes() {
    Student student = createStudent();
    List<Course> courses = createCourses();
    List<Historial> mixedHistorials = createMixedStatusHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentIdAndStatus(STUDENT_ID, HistorialStatus.FINISHED))
        .thenReturn(mixedHistorials);
    when(groupService.getGroupByGroupCode("GRP001")).thenReturn(createGroup("MATH101"));

    double result = pemsumService.getCompletedCoursesPercentage(STUDENT_ID);

    assertEquals(33.33, result, 0.01);
  }

  @Test
  void getStudentCoursesStatus_ShouldReturnStatusMap() {
    Student student = createStudent();
    List<Course> courses = createCourses();
    List<Historial> historials = createHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentId(STUDENT_ID)).thenReturn(historials);

    Map<String, String> result = pemsumService.getStudentCoursesStatus(STUDENT_ID);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals("FINISHED", result.get("MATH101"));
    assertEquals("ON_GOING", result.get("PHYS101"));
    assertEquals("PENDING", result.get("CHEM101"));
  }

  @Test
  void getStudentCoursesStatus_ShouldSetPending_ForCoursesWithoutHistorial() {
    Student student = createStudent();
    List<Course> courses = createCourses();
    List<Historial> historials = new ArrayList<>();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentId(STUDENT_ID)).thenReturn(historials);

    Map<String, String> result = pemsumService.getStudentCoursesStatus(STUDENT_ID);

    assertEquals("PENDING", result.get("MATH101"));
    assertEquals("PENDING", result.get("PHYS101"));
    assertEquals("PENDING", result.get("CHEM101"));
  }

  @Test
  void getStudentCoursesStatus_ShouldThrowException_WhenNoCoursesFound() {
    Student student = createStudent();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN))
        .thenReturn(new ArrayList<>());

    assertThrows(
        ResourceNotFoundException.class, () -> pemsumService.getStudentCoursesStatus(STUDENT_ID));
  }

  @Test
  void getStudentCoursesStatus_ShouldHandleAllHistorialStatusTypes() {
    Student student = createStudent();
    List<Course> courses = createCourses();
    List<Historial> allStatusHistorials = createAllStatusHistorials();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentId(STUDENT_ID)).thenReturn(allStatusHistorials);

    Map<String, String> result = pemsumService.getStudentCoursesStatus(STUDENT_ID);

    assertEquals("FINISHED", result.get("MATH101"));
    assertEquals("ON_GOING", result.get("PHYS101"));
    assertEquals("SWAPPED", result.get("CHEM101"));
  }

  @Test
  void getStudentCoursesStatus_ShouldPrioritizeFinishedStatus() {
    Student student = createStudent();
    List<Course> courses = createSingleCourse();
    List<Historial> multipleHistorials = createMultipleHistorialsForSameCourse();

    when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);
    when(facultyService.findCoursesByFacultyNameAndPlan(FACULTY_NAME, PLAN)).thenReturn(courses);
    when(historialService.getHistorialByStudentId(STUDENT_ID)).thenReturn(multipleHistorials);

    Map<String, String> result = pemsumService.getStudentCoursesStatus(STUDENT_ID);

    assertEquals("FINISHED", result.get("MATH101"));
  }

  @Test
  void getApprovedCredits_ShouldCalculateOnlyFinishedStatus() {
    List<CourseStatus> coursesList = createMixedCourseStatusList();

    PemsumService service =
        new PemsumService(facultyService, studentService, historialService, groupService);

    int result = service.getApprovedCredits(coursesList);

    assertEquals(6, result);
  }

  @Test
  void getCoursesList_ShouldMapCoursesWithCorrectStatus() {
    List<Course> courses = createCourses();
    List<Historial> historials = createHistorials();

    PemsumService service =
        new PemsumService(facultyService, studentService, historialService, groupService);

    List<CourseStatus> result = service.getCoursesList(courses, historials);

    assertEquals(3, result.size());
    assertEquals("MATH101", result.get(0).getCourse().getAbbreviation());
    assertEquals("FINISHED", result.get(0).getStatus());
    assertEquals("PHYS101", result.get(1).getCourse().getAbbreviation());
    assertEquals("ON_GOING", result.get(1).getStatus());
    assertEquals("CHEM101", result.get(2).getCourse().getAbbreviation());
    assertEquals("pending", result.get(2).getStatus());
  }

  private Student createStudent() {
    Student student = new Student();
    student.setId(STUDENT_ID);
    student.setFullName("John Doe");
    student.setFacultyName(FACULTY_NAME);
    student.setPlan(PLAN);
    return student;
  }

  private List<Course> createCourses() {
    Course math = new Course("MATH101", "Mathematics");
    math.setCredits(3);
    math.setSemester("1");

    Course physics = new Course("PHYS101", "Physics");
    physics.setCredits(3);
    physics.setSemester("1");

    Course chemistry = new Course("CHEM101", "Chemistry");
    chemistry.setCredits(3);
    chemistry.setSemester("2");

    return List.of(math, physics, chemistry);
  }

  private List<Course> createCoursesWithRequisites() {
    Course math = new Course("MATH101", "Mathematics");
    math.setCredits(4);
    math.setSemester("1");

    Course physics = new Course("PHYS101", "Physics");
    physics.setCredits(4);
    physics.setSemester("2");
    physics.setRequisites(List.of("MATH101"));

    Course chemistry = new Course("CHEM101", "Chemistry");
    chemistry.setCredits(4);
    chemistry.setSemester("3");
    chemistry.setRequisites(List.of("PHYS101"));

    return List.of(math, physics, chemistry);
  }

  private List<Course> createCoursesWithSemester() {
    Course math = new Course("MATH101", "Mathematics");
    math.setCredits(3);
    math.setSemester("1");

    Course physics = new Course("PHYS101", "Physics");
    physics.setCredits(3);
    physics.setSemester("2");

    Course chemistry = new Course("CHEM101", "Chemistry");
    chemistry.setCredits(3);
    chemistry.setSemester("3");

    return List.of(math, physics, chemistry);
  }

  private List<Course> createCoursesWithDifferentCredits() {
    Course math = new Course("MATH101", "Mathematics");
    math.setCredits(4);

    Course physics = new Course("PHYS101", "Physics");
    physics.setCredits(2);

    Course chemistry = new Course("CHEM101", "Chemistry");
    chemistry.setCredits(6);

    return List.of(math, physics, chemistry);
  }

  private List<Course> createSingleCourse() {
    Course math = new Course("MATH101", "Mathematics");
    math.setCredits(3);

    return List.of(math);
  }

  private List<Historial> createHistorials() {
    Historial mathHistorial = new Historial();
    mathHistorial.setGroupCode("MATH101");
    mathHistorial.setStatus(HistorialStatus.FINISHED);

    Historial physicsHistorial = new Historial();
    physicsHistorial.setGroupCode("PHYS101");
    physicsHistorial.setStatus(HistorialStatus.ON_GOING);

    return List.of(mathHistorial, physicsHistorial);
  }

  private List<Historial> createFinishedHistorials() {
    Historial mathHistorial = new Historial();
    mathHistorial.setGroupCode("GRP001");

    Historial physicsHistorial = new Historial();
    physicsHistorial.setGroupCode("GRP002");

    return List.of(mathHistorial, physicsHistorial);
  }

  private List<Historial> createMixedStatusHistorials() {
    Historial mathHistorial = new Historial();
    mathHistorial.setGroupCode("GRP001");

    return List.of(mathHistorial);
  }

  private List<Historial> createAllStatusHistorials() {
    Historial mathHistorial = new Historial();
    mathHistorial.setGroupCode("MATH101");
    mathHistorial.setStatus(HistorialStatus.FINISHED);

    Historial physicsHistorial = new Historial();
    physicsHistorial.setGroupCode("PHYS101");
    physicsHistorial.setStatus(HistorialStatus.ON_GOING);

    Historial chemHistorial = new Historial();
    chemHistorial.setGroupCode("CHEM101");
    chemHistorial.setStatus(HistorialStatus.SWAPPED);

    return List.of(mathHistorial, physicsHistorial, chemHistorial);
  }

  private List<Historial> createMultipleHistorialsForSameCourse() {
    Historial failedHistorial = new Historial();
    failedHistorial.setGroupCode("MATH101");
    failedHistorial.setStatus(HistorialStatus.FAILED);

    Historial finishedHistorial = new Historial();
    finishedHistorial.setGroupCode("MATH101");
    finishedHistorial.setStatus(HistorialStatus.FINISHED);

    Historial ongoingHistorial = new Historial();
    ongoingHistorial.setGroupCode("MATH101");
    ongoingHistorial.setStatus(HistorialStatus.ON_GOING);

    return List.of(failedHistorial, finishedHistorial, ongoingHistorial);
  }

  private Group createGroup(String abbreviation) {
    Group group = new Group();
    group.setAbbreviation(abbreviation);
    return group;
  }

  private List<CourseStatus> createMixedCourseStatusList() {
    Course math = new Course("MATH101", "Mathematics");
    math.setCredits(3);

    Course physics = new Course("PHYS101", "Physics");
    physics.setCredits(3);

    Course chemistry = new Course("CHEM101", "Chemistry");
    chemistry.setCredits(3);

    return List.of(
        new CourseStatus(math, "FINISHED"),
        new CourseStatus(physics, "FINISHED"),
        new CourseStatus(chemistry, "ON_GOING"),
        new CourseStatus(new Course("SWAP101", "Swapped"), "SWAPPED"),
        new CourseStatus(new Course("CANC101", "Cancelled"), "CANCELLED"),
        new CourseStatus(new Course("FAIL101", "Failed"), "FAILED"));
  }
}
