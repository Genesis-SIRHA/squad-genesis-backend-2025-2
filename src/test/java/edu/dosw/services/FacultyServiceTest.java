package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.repositories.FacultyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link FacultyService}. */
class FacultyServiceTest {

  @Mock private FacultyRepository facultyRepository;

  @Mock private GroupService groupService;

  @InjectMocks private FacultyService facultyService;

  private Faculty faculty;
  private Course course;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    course = new Course("CS101", "Algorithms", 4);
    faculty = new Faculty("Engineering", "2025", List.of(course));
  }

  //
  //  @Test
  //  void getAllFacultyNames_shouldReturnMap() {
  //    when(facultyRepository.findAll()).thenReturn(List.of(faculty));
  //
  //    var result = facultyService.getAllFacultyNames();
  //
  //    assertEquals(1, result.size());
  //    assertEquals("2025", result.get("Engineering"));
  //  }

  @Test
  void findCoursesByFacultyNameAndPlan_shouldReturnCourses() {
    when(facultyRepository.findByNameAndPlan("Engineering", "2025"))
        .thenReturn(Optional.of(faculty));

    var result = facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2025");

    assertEquals(1, result.size());
    assertEquals("CS101", result.get(0).getAbbreviation());
  }

  @Test
  void findCoursesByFacultyNameAndPlan_shouldThrowIfFacultyNotFound() {
    when(facultyRepository.findByNameAndPlan("Invalid", "2025")).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> facultyService.findCoursesByFacultyNameAndPlan("Invalid", "2025"));

    assertEquals("Faculty not found: Invalid", ex.getMessage());
  }
  //
  //  @Test
  //  void createCourse_shouldSaveAndReturnFaculty() {
  //    CourseRequest request = new CourseRequest("CS102", "Data Structures", 3, "Engineering",
  // "2025");
  //    when(facultyRepository.findByNameAndPlan("Engineering", "2025"))
  //        .thenReturn(Optional.of(faculty));
  //    when(facultyRepository.save(any(Faculty.class))).thenAnswer(i -> i.getArgument(0));
  //
  //    Faculty result = facultyService.addCourse(request);
  //
  //    assertNotNull(result);
  //    assertTrue(result.getCourses().stream().anyMatch(c -> c.getAbbreviation().equals("CS102")));
  //  }

  //  @Test
  //  void createCourse_shouldThrowIfCourseExists() {
  //    CourseRequest request = new CourseRequest("CS101", "Algorithms", 4, "Engineering", "2025");
  //    when(facultyRepository.findByNameAndPlan("Engineering", "2025"))
  //        .thenReturn(Optional.of(faculty));
  //
  //    BusinessException ex =
  //        assertThrows(BusinessException.class, () -> facultyService.addCourse(request));
  //
  //    assertEquals("Course already exists: CS101", ex.getMessage());
  //  }

  //  @Test
  //  void updateCourse_shouldSaveUpdatedFaculty() {
  //    CourseRequest request = new CourseRequest("CS103", "New Course", 5, "Engineering", "2025");
  //    when(facultyRepository.findByNameAndPlan("Engineering", "2025"))
  //        .thenReturn(Optional.of(faculty));
  //    when(facultyRepository.save(any(Faculty.class))).thenAnswer(i -> i.getArgument(0));
  //
  //    Course result = facultyService.updateCourse("CS101", request);
  //
  //    assertNotNull(result);
  //    assertTrue(result.getCourseName().equals("New Course"));
  //  }
  //
  //  @Test
  //  void updateCourse_shouldThrowIfFacultyNotFound() {
  //    CourseRequest request = new CourseRequest("CS103", "New Course", 5, "Invalid", "2025");
  //    when(facultyRepository.findByNameAndPlan("Invalid", "2025")).thenReturn(Optional.empty());
  //
  //    BusinessException ex =
  //        assertThrows(BusinessException.class, () -> facultyService.updateCourse("CS101",
  // request));
  //
  //    assertEquals("Faculty not found: Invalid", ex.getMessage());
  //  }

  //  @Test
  //  void addGroupToCourse_shouldReturnTrueIfGroupAdded() {
  //    when(facultyRepository.findAll()).thenReturn(List.of(faculty));
  //    GroupRequest groupRequest = new GroupRequest("G1", "CS101", "2025", "1", "T1", true, 1, 30,
  // 0);
  //    when(groupService.createGroup(groupRequest)).thenReturn(new Group());
  //
  //    Boolean result = facultyService.addGroupToCourse(groupRequest);
  //
  //    assertTrue(result);
  //  }

  //  @Test
  //  void addGroupToCourse_shouldThrowIfCourseNotFound() {
  //    when(facultyRepository.findAll()).thenReturn(List.of()); // no faculties
  //
  //    GroupRequest groupRequest =
  //        new GroupRequest("G1", "INVALID", "2025", "1", "T1", true, 1, 30, 0);
  //
  //    BusinessException ex =
  //        assertThrows(BusinessException.class, () ->
  // facultyService.addGroupToCourse(groupRequest));
  //
  //    assertEquals("Faculty not found: INVALID", ex.getMessage());
  //  }

  //  @Test
  //  void deleteCourse_shouldCallRepositoryDelete() {
  //    doNothing().when(facultyRepository).deleteById("CS101");
  //
  //    facultyService.deleteCourse("CS101");
  //
  //    verify(facultyRepository, times(1)).deleteById("CS101");
  //  }

  //  @Test
  //  void findCourseByCode_shouldReturnCourseIfExists() {
  //    when(facultyRepository.findAll()).thenReturn(List.of(faculty));
  //
  //    Optional<Course> result = facultyService.findCourseByCode("CS101");
  //
  //    assertTrue(result.isPresent());
  //    assertEquals("CS101", result.get().getAbbreviation());
  //  }

  //  @Test
  //  void findCourseByCode_shouldReturnEmptyIfNotFound() {
  //    when(facultyRepository.findAll()).thenReturn(List.of(faculty));
  //
  //    Optional<Course> result = facultyService.findCourseByCode("INVALID");
  //
  //    assertTrue(result.isEmpty());
  //  }
}
