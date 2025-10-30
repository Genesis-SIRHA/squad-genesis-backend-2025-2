package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.HistorialDTO;
import edu.dosw.dto.SessionDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.model.Historial;
import edu.dosw.model.Session;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.repositories.GroupRepository;
import edu.dosw.services.Validators.GroupValidator;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  @Mock private FacultyService facultyService;
  @Mock private GroupRepository groupRepository;
  @Mock private PeriodService periodService;
  @Mock private SessionService sessionService;
  @Mock private HistorialService historialService;
  @Mock private GroupValidator groupValidator;

  @InjectMocks private GroupService groupService;

  @Test
  void getAllGroupsByCourseAbbreviation_ShouldReturnGroups() {
    List<Group> expectedGroups = Arrays.asList(createGroup("GROUP1"), createGroup("GROUP2"));
    when(groupRepository.findAllByAbbreviation("CS101")).thenReturn(expectedGroups);

    List<Group> result = groupService.getAllGroupsByCourseAbbreviation("CS101");

    assertEquals(2, result.size());
    verify(groupRepository).findAllByAbbreviation("CS101");
  }

  @Test
  void getGroupByGroupCode_WithExistingGroup_ShouldReturnGroup() {
    Group expectedGroup = createGroup("GROUP1");
    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(expectedGroup));

    Group result = groupService.getGroupByGroupCode("GROUP1");

    assertNotNull(result);
    assertEquals("GROUP1", result.getGroupCode());
    verify(groupRepository).findByGroupCode("GROUP1");
  }

  @Test
  void getGroupByGroupCode_WithNonExistingGroup_ShouldThrowResourceNotFoundException() {
    when(groupRepository.findByGroupCode("NONEXISTENT")).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> groupService.getGroupByGroupCode("NONEXISTENT"));

    assertEquals("Group not found: NONEXISTENT", exception.getMessage());
    verify(groupRepository).findByGroupCode("NONEXISTENT");
  }

  @Test
  void createGroup_WithValidData_ShouldReturnSavedGroup() {
    CreationGroupRequest request =
        new CreationGroupRequest("GROUP1", "CS101", "PROF123", false, "1", 30, 0);
    Course course = createCourse("CS101");
    Group group = createGroup("GROUP1");

    when(facultyService.findCourseByAbbreviation("CS101", "Engineering", "2024"))
        .thenReturn(course);
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(groupRepository.save(any(Group.class))).thenReturn(group);

    Group result = groupService.createGroup(request, "Engineering", "2024");

    assertNotNull(result);
    verify(facultyService).findCourseByAbbreviation("CS101", "Engineering", "2024");
    verify(groupRepository).save(any(Group.class));
  }

  @Test
  void createGroup_WithNonExistingCourse_ShouldThrowResourceNotFoundException() {
    CreationGroupRequest request =
        new CreationGroupRequest("GROUP1", "CS101", "PROF123", false, "1", 30, 0);
    when(facultyService.findCourseByAbbreviation("CS101", "Engineering", "2024")).thenReturn(null);

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> groupService.createGroup(request, "Engineering", "2024"));

    assertEquals("Faculty not found: CS101", exception.getMessage());
    verify(facultyService).findCourseByAbbreviation("CS101", "Engineering", "2024");
    verify(groupRepository, never()).save(any());
  }

  @Test
  void updateGroup_WithExistingGroup_ShouldReturnUpdatedGroup() {
    UpdateGroupRequest request = new UpdateGroupRequest("PROF456", true, "2", 40, 25);
    Group existingGroup = createGroup("GROUP1");
    Group updatedGroup = createGroup("GROUP1");

    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(existingGroup));
    when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);

    Group result = groupService.updateGroup("GROUP1", request);

    assertNotNull(result);
    verify(groupRepository).findByGroupCode("GROUP1");
    verify(groupRepository).save(existingGroup);
  }

  @Test
  void updateGroup_WithNonExistingGroup_ShouldThrowResourceNotFoundException() {
    UpdateGroupRequest request = new UpdateGroupRequest("PROF456", true, "2", 40, 25);
    when(groupRepository.findByGroupCode("NONEXISTENT")).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> groupService.updateGroup("NONEXISTENT", request));

    assertEquals("Group not found: NONEXISTENT", exception.getMessage());
    verify(groupRepository).findByGroupCode("NONEXISTENT");
    verify(groupRepository, never()).save(any());
  }

  @Test
  void deleteGroup_WithExistingGroup_ShouldReturnDeletedGroup() {
    Group group = createGroup("GROUP1");
    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(group));
    doNothing().when(groupRepository).delete(group);
    doNothing().when(sessionService).deleteSessionsByGroupCode("GROUP1");

    Group result = groupService.deleteGroup("GROUP1");

    assertNotNull(result);
    verify(groupRepository).delete(group);
    verify(sessionService).deleteSessionsByGroupCode("GROUP1");
  }

  @Test
  void deleteGroup_WhenDeletionFails_ShouldThrowBusinessException() {
    Group group = createGroup("GROUP1");
    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(group));
    doThrow(new RuntimeException("DB error")).when(groupRepository).delete(group);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> groupService.deleteGroup("GROUP1"));

    assertTrue(exception.getMessage().contains("Failed to delete group"));
    verify(groupRepository).delete(group);
  }

  @Test
  void addSession_WithValidData_ShouldReturnSession() {
    SessionDTO sessionDTO = new SessionDTO("GROUP1", "CLASSROOM_A", 1, null);
    Session expectedSession = new Session();
    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(createGroup("GROUP1")));
    when(sessionService.createSession(sessionDTO)).thenReturn(expectedSession);

    Session result = groupService.addSession(sessionDTO);

    assertNotNull(result);
    verify(groupRepository).findByGroupCode("GROUP1");
    verify(sessionService).createSession(sessionDTO);
  }

  @Test
  void updateSession_WithValidData_ShouldReturnUpdatedSession() {
    SessionDTO sessionDTO = new SessionDTO("GROUP1", "CLASSROOM_B", 2, null);
    Session expectedSession = new Session();
    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(createGroup("GROUP1")));
    when(sessionService.updateSession("SESSION1", sessionDTO)).thenReturn(expectedSession);

    Session result = groupService.updateSession("SESSION1", sessionDTO);

    assertNotNull(result);
    verify(groupRepository).findByGroupCode("GROUP1");
    verify(sessionService).updateSession("SESSION1", sessionDTO);
  }

  @Test
  void getSessionBySessionId_ShouldReturnSession() {
    Session expectedSession = new Session();
    when(sessionService.getSessionBySessionId("SESSION1")).thenReturn(expectedSession);

    Session result = groupService.getSessionBySessionId("SESSION1");

    assertNotNull(result);
    verify(sessionService).getSessionBySessionId("SESSION1");
  }

  @Test
  void getSessionsByGroupCode_ShouldReturnSessions() {
    List<Session> expectedSessions = Arrays.asList(new Session(), new Session());
    when(sessionService.getSessionsByGroupCode("GROUP1")).thenReturn(expectedSessions);

    List<Session> result = groupService.getSessionsByGroupCode("GROUP1");

    assertEquals(2, result.size());
    verify(sessionService).getSessionsByGroupCode("GROUP1");
  }

  @Test
  void deleteSession_ShouldReturnDeletedSession() {
    Session expectedSession = new Session();
    when(sessionService.deleteSession("SESSION1")).thenReturn(expectedSession);

    Session result = groupService.deleteSession("SESSION1");

    assertNotNull(result);
    verify(sessionService).deleteSession("SESSION1");
  }

  @Test
  void deleteSessionsFromGroup_ShouldReturnGroup() {
    Group group = createGroup("GROUP1");
    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(group));
    doNothing().when(sessionService).deleteSessionsByGroupCode("GROUP1");

    Group result = groupService.deleteSessionsFromGroup("GROUP1");

    assertNotNull(result);
    verify(sessionService).deleteSessionsByGroupCode("GROUP1");
  }

  @Test
  void addStudent_WithValidData_ShouldReturnUpdatedGroup() {
    Group group = createGroup("GROUP1");
    group.setEnrolled(0);
    HistorialDTO historialDTO = new HistorialDTO("STUD123", "GROUP1", HistorialStatus.ON_GOING);

    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(group));
    doNothing().when(groupValidator).validateAddStudentToGroup(group, "STUD123");
    when(groupRepository.save(group)).thenReturn(group);
    when(historialService.addHistorial(historialDTO)).thenReturn(null);

    Group result = groupService.addStudent("GROUP1", "STUD123");

    assertNotNull(result);
    assertEquals(1, result.getEnrolled());
    verify(groupValidator).validateAddStudentToGroup(group, "STUD123");
    verify(historialService).addHistorial(historialDTO);
  }

  @Test
  void addStudent_WhenHistorialAddFails_ShouldUpdateHistorial() {
    Group group = createGroup("GROUP1");
    group.setEnrolled(0);
    HistorialDTO historialDTO = new HistorialDTO("STUD123", "GROUP1", HistorialStatus.ON_GOING);

    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(group));
    doNothing().when(groupValidator).validateAddStudentToGroup(group, "STUD123");
    when(groupRepository.save(group)).thenReturn(group);

    Historial mockHistorial = mock(Historial.class);
    when(historialService.addHistorial(historialDTO))
        .thenThrow(new BusinessException("Historial error"));
    when(historialService.updateHistorial("STUD123", "GROUP1", HistorialStatus.ON_GOING))
        .thenReturn(mockHistorial);

    Group result = groupService.addStudent("GROUP1", "STUD123");

    assertNotNull(result);
    assertEquals(1, result.getEnrolled());
    verify(groupValidator).validateAddStudentToGroup(group, "STUD123");
    verify(historialService).addHistorial(historialDTO);
    verify(historialService).updateHistorial("STUD123", "GROUP1", HistorialStatus.ON_GOING);
  }

  @Test
  void addStudent_WhenBothHistorialOperationsFail_ShouldThrowBusinessException() {
    Group group = createGroup("GROUP1");
    group.setEnrolled(0);
    HistorialDTO historialDTO = new HistorialDTO("STUD123", "GROUP1", HistorialStatus.ON_GOING);

    when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(group));
    doNothing().when(groupValidator).validateAddStudentToGroup(group, "STUD123");
    when(groupRepository.save(group)).thenReturn(group);
    when(historialService.addHistorial(historialDTO))
        .thenThrow(new BusinessException("Add failed"));
    when(historialService.updateHistorial("STUD123", "GROUP1", HistorialStatus.ON_GOING))
        .thenThrow(new BusinessException("Update failed"));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> groupService.addStudent("GROUP1", "STUD123"));

    assertNotNull(exception);
    verify(groupValidator).validateAddStudentToGroup(group, "STUD123");
    verify(historialService).addHistorial(historialDTO);
    verify(historialService).updateHistorial("STUD123", "GROUP1", HistorialStatus.ON_GOING);
  }

  @Test
  void deleteStudent_WithValidData_ShouldReturnUpdatedGroup() {
    String groupCode = "GROUP1";
    String studentId = "STUD123";
    Group group = createGroup(groupCode);
    group.setEnrolled(2);
    group.setYear("2024");
    group.setPeriod("1");

    when(groupRepository.findByGroupCode(groupCode)).thenReturn(Optional.of(group));
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(groupRepository.save(group)).thenReturn(group);

    Historial mockHistorial = mock(Historial.class);
    when(historialService.updateHistorial(studentId, groupCode, HistorialStatus.CANCELLED))
        .thenReturn(mockHistorial);

    Group result = groupService.deleteStudent(groupCode, studentId);

    assertNotNull(result);
    assertEquals(1, result.getEnrolled());
    verify(groupRepository).findByGroupCode(groupCode);
    verify(periodService, atLeastOnce()).getYear();
    verify(periodService, atLeastOnce()).getPeriod();
    verify(groupRepository).save(group);
    verify(historialService).updateHistorial(studentId, groupCode, HistorialStatus.CANCELLED);
  }

  @Test
  void deleteStudent_WithPeriodMismatch_ShouldThrowBusinessException() {
    String groupCode = "GROUP1";
    String studentId = "STUD123";
    Group group = createGroup(groupCode);
    group.setEnrolled(2);
    group.setYear("2024");
    group.setPeriod("1");

    when(groupRepository.findByGroupCode(groupCode)).thenReturn(Optional.of(group));
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("2");

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> groupService.deleteStudent(groupCode, studentId));

    assertTrue(exception.getMessage().contains("The historial period and year does not match"));
    verify(groupRepository).findByGroupCode(groupCode);
    verify(periodService, atLeastOnce()).getYear();
    verify(periodService, atLeastOnce()).getPeriod();
    verify(groupRepository, never()).save(any());
    verify(historialService, never()).updateHistorial(any(), any(), any());
  }

  @Test
  void deleteStudent_WithYearMismatch_ShouldThrowBusinessException() {
    String groupCode = "GROUP1";
    String studentId = "STUD123";
    Group group = createGroup(groupCode);
    group.setEnrolled(2);
    group.setYear("2023");
    group.setPeriod("1");

    when(groupRepository.findByGroupCode(groupCode)).thenReturn(Optional.of(group));
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> groupService.deleteStudent(groupCode, studentId));

    assertTrue(exception.getMessage().contains("The historial period and year does not match"));
    verify(groupRepository).findByGroupCode(groupCode);
    verify(periodService, atLeastOnce()).getYear();
    verify(periodService, atLeastOnce()).getPeriod();
    verify(groupRepository, never()).save(any());
    verify(historialService, never()).updateHistorial(any(), any(), any());
  }

  @Test
  void deleteStudent_WhenHistorialUpdateFails_ShouldThrowBusinessException() {
    String groupCode = "GROUP1";
    String studentId = "STUD123";
    Group group = createGroup(groupCode);
    group.setEnrolled(2);
    group.setYear("2024");
    group.setPeriod("1");

    when(groupRepository.findByGroupCode(groupCode)).thenReturn(Optional.of(group));
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");
    when(groupRepository.save(group)).thenReturn(group);
    when(historialService.updateHistorial(studentId, groupCode, HistorialStatus.CANCELLED))
        .thenThrow(new BusinessException("Historial update failed"));

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> groupService.deleteStudent(groupCode, studentId));

    assertTrue(exception.getMessage().contains("Failed to update historial"));
    verify(groupRepository).findByGroupCode(groupCode);
    verify(periodService, atLeastOnce()).getYear();
    verify(periodService, atLeastOnce()).getPeriod();
    verify(groupRepository).save(group);
    verify(historialService).updateHistorial(studentId, groupCode, HistorialStatus.CANCELLED);
  }

  @Test
  void getCourseByGroupCode_WithValidGroupCode_ShouldReturnCourse() {
    String groupCode = "GROUP1";
    Faculty faculty = new Faculty();
    Course course = new Course();
    course.setAbbreviation("CS101");
    faculty.setCourses(Arrays.asList(course));

    Group group = createGroup(groupCode);
    group.setAbbreviation("CS101");

    when(facultyService.getAllFaculties()).thenReturn(Arrays.asList(faculty));
    when(groupRepository.findAllByCourseId("CS101")).thenReturn(Arrays.asList(group));

    Course result = groupService.getCourseByGroupCode(groupCode);

    assertNotNull(result);
    assertEquals("CS101", result.getAbbreviation());
    verify(facultyService).getAllFaculties();
    verify(groupRepository).findAllByCourseId("CS101");
  }

  @Test
  void getCourseByGroupCode_WithEmptyGroupCode_ShouldThrowBusinessException() {
    BusinessException exception =
        assertThrows(BusinessException.class, () -> groupService.getCourseByGroupCode(""));

    assertEquals("Group code cannot be empty", exception.getMessage());
  }

  @Test
  void getCourseByGroupCode_WithNullGroupCode_ShouldThrowBusinessException() {
    BusinessException exception =
        assertThrows(BusinessException.class, () -> groupService.getCourseByGroupCode(null));

    assertEquals("Group code cannot be empty", exception.getMessage());
  }

  @Test
  void getCourseByGroupCode_WithNonExistingGroupCode_ShouldThrowBusinessException() {
    String groupCode = "NONEXISTENT";
    Faculty faculty = new Faculty();
    Course course = new Course();
    course.setAbbreviation("CS101");
    faculty.setCourses(Arrays.asList(course));

    when(facultyService.getAllFaculties()).thenReturn(Arrays.asList(faculty));
    when(groupRepository.findAllByCourseId("CS101"))
        .thenReturn(Arrays.asList(createGroup("OTHER_GROUP")));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> groupService.getCourseByGroupCode(groupCode));

    assertEquals("Group not found with code: " + groupCode, exception.getMessage());
    verify(facultyService).getAllFaculties();
    verify(groupRepository).findAllByCourseId("CS101");
  }

  @Test
  void getCourseByGroupCode_WithNullCoursesInFaculty_ShouldContinueSearch() {
    String groupCode = "GROUP1";
    Faculty faculty1 = new Faculty();
    faculty1.setCourses(null);

    Faculty faculty2 = new Faculty();
    Course course = new Course();
    course.setAbbreviation("CS101");
    faculty2.setCourses(Arrays.asList(course));

    Group group = createGroup(groupCode);
    group.setAbbreviation("CS101");

    when(facultyService.getAllFaculties()).thenReturn(Arrays.asList(faculty1, faculty2));
    when(groupRepository.findAllByCourseId("CS101")).thenReturn(Arrays.asList(group));

    Course result = groupService.getCourseByGroupCode(groupCode);

    assertNotNull(result);
    assertEquals("CS101", result.getAbbreviation());
    verify(facultyService).getAllFaculties();
    verify(groupRepository).findAllByCourseId("CS101");
  }

  @Test
  void getCourseByGroupCode_WithEmptyGroupsForCourse_ShouldContinueSearch() {
    String groupCode = "GROUP1";
    Faculty faculty = new Faculty();
    Course course1 = new Course();
    course1.setAbbreviation("CS101");
    Course course2 = new Course();
    course2.setAbbreviation("MATH101");
    faculty.setCourses(Arrays.asList(course1, course2));

    Group group = createGroup(groupCode);
    group.setAbbreviation("MATH101");

    when(facultyService.getAllFaculties()).thenReturn(Arrays.asList(faculty));
    when(groupRepository.findAllByCourseId("CS101")).thenReturn(Arrays.asList());
    when(groupRepository.findAllByCourseId("MATH101")).thenReturn(Arrays.asList(group));

    Course result = groupService.getCourseByGroupCode(groupCode);

    assertNotNull(result);
    assertEquals("MATH101", result.getAbbreviation());
    verify(facultyService).getAllFaculties();
    verify(groupRepository).findAllByCourseId("CS101");
    verify(groupRepository).findAllByCourseId("MATH101");
  }

  private Group createGroup(String groupCode) {
    return new Group("ID123", groupCode, "CS101", "2024", "1", "PROF123", false, "1", 0, 30);
  }

  private Course createCourse(String abbreviation) {
    Course course = new Course();
    course.setAbbreviation(abbreviation);
    return course;
  }
}
