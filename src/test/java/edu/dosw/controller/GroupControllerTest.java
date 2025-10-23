package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.SessionDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.model.Group;
import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import edu.dosw.services.GroupService;
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
class GroupControllerTest {

  @Mock private GroupService groupService;

  @InjectMocks private GroupController groupController;

  @Test
  void getGroupByCode_WhenGroupExists_ShouldReturnGroup() {
    String groupCode = "GROUP001";
    Group expectedGroup =
        new Group.GroupBuilder()
            .groupCode(groupCode)
            .abbreviation("MATH101")
            .year("2024")
            .period("1")
            .professorId("PROF001")
            .isLab(false)
            .groupNum("01")
            .enrolled(30)
            .maxCapacity(40)
            .build();

    when(groupService.getGroupByGroupCode(groupCode)).thenReturn(expectedGroup);

    ResponseEntity<Group> response = groupController.getGroupByCode(groupCode);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(groupCode, response.getBody().getGroupCode());
    verify(groupService, times(1)).getGroupByGroupCode(groupCode);
  }

  @Test
  void getGroupByCode_WhenGroupNotExists_ShouldThrowException() {
    String groupCode = "NONEXISTENT";
    when(groupService.getGroupByGroupCode(groupCode))
        .thenThrow(new RuntimeException("Group not found"));

    assertThrows(RuntimeException.class, () -> groupController.getGroupByCode(groupCode));
    verify(groupService, times(1)).getGroupByGroupCode(groupCode);
  }

  @Test
  void createGroup_WithValidData_ShouldReturnGroup() {
    CreationGroupRequest groupRequest =
        new CreationGroupRequest("GROUP001", "MATH101", "PROF001", false, "01", 30, 40);
    String facultyName = "Engineering";
    String plan = "2024";
    Group createdGroup =
        new Group.GroupBuilder()
            .groupCode("GROUP001")
            .abbreviation("MATH101")
            .year("2024")
            .period("1")
            .professorId("PROF001")
            .isLab(false)
            .groupNum("01")
            .enrolled(30)
            .maxCapacity(40)
            .build();

    when(groupService.createGroup(groupRequest, facultyName, plan)).thenReturn(createdGroup);

    ResponseEntity<Group> response = groupController.createGroup(groupRequest, facultyName, plan);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("GROUP001", response.getBody().getGroupCode());
    verify(groupService, times(1)).createGroup(groupRequest, facultyName, plan);
  }

  @Test
  void updateGroup_WithValidData_ShouldReturnUpdatedGroup() {
    String groupCode = "GROUP001";
    UpdateGroupRequest updateRequest = new UpdateGroupRequest("PROF002", true, "02", 35, 25);
    Group updatedGroup =
        new Group.GroupBuilder()
            .groupCode(groupCode)
            .abbreviation("MATH101")
            .year("2024")
            .period("1")
            .professorId("PROF002")
            .isLab(true)
            .groupNum("02")
            .enrolled(25)
            .maxCapacity(35)
            .build();

    when(groupService.updateGroup(groupCode, updateRequest)).thenReturn(updatedGroup);

    ResponseEntity<Group> response = groupController.updateGroup(groupCode, updateRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("PROF002", response.getBody().getProfessorId());
    verify(groupService, times(1)).updateGroup(groupCode, updateRequest);
  }

  @Test
  void deleteGroup_WhenGroupExists_ShouldReturnDeletedGroup() {
    String groupCode = "GROUP001";
    Group deletedGroup =
        new Group.GroupBuilder()
            .groupCode(groupCode)
            .abbreviation("MATH101")
            .year("2024")
            .period("1")
            .professorId("PROF001")
            .isLab(false)
            .groupNum("01")
            .enrolled(30)
            .maxCapacity(40)
            .build();

    when(groupService.deleteGroup(groupCode)).thenReturn(deletedGroup);

    ResponseEntity<Group> response = groupController.deleteGroup(groupCode);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(groupCode, response.getBody().getGroupCode());
    verify(groupService, times(1)).deleteGroup(groupCode);
  }

  @Test
  void getSessionsByGroupCode_WhenSessionsExist_ShouldReturnSessions() {
    String groupCode = "GROUP001";
    Session session1 =
        new Session.SessionBuilder()
            .groupCode(groupCode)
            .classroomName("A101")
            .slot(1)
            .day(DayOfWeek.MONDAY)
            .year("2024")
            .period("1")
            .build();
    Session session2 =
        new Session.SessionBuilder()
            .groupCode(groupCode)
            .classroomName("A102")
            .slot(2)
            .day(DayOfWeek.WEDNESDAY)
            .year("2024")
            .period("1")
            .build();
    List<Session> expectedSessions = Arrays.asList(session1, session2);

    when(groupService.getSessionsByGroupCode(groupCode)).thenReturn(expectedSessions);

    ResponseEntity<List<Session>> response =
        groupController.getSessionsByStudentIdAndGroupCode(groupCode);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    verify(groupService, times(1)).getSessionsByGroupCode(groupCode);
  }

  @Test
  void getSessionBySessionId_WhenSessionExists_ShouldReturnSession() {
    String sessionId = "SESS001";
    Session expectedSession =
        new Session.SessionBuilder()
            .groupCode("GROUP001")
            .classroomName("A101")
            .slot(1)
            .day(DayOfWeek.MONDAY)
            .year("2024")
            .period("1")
            .build();

    when(groupService.getSessionBySessionId(sessionId)).thenReturn(expectedSession);

    ResponseEntity<Session> response = groupController.getSessionBySessionId(sessionId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("GROUP001", response.getBody().getGroupCode());
    verify(groupService, times(1)).getSessionBySessionId(sessionId);
  }

  @Test
  void createSession_WithValidData_ShouldReturnSession() {
    SessionDTO sessionDto = new SessionDTO("GROUP001", "A101", 1, DayOfWeek.MONDAY);
    Session createdSession =
        new Session.SessionBuilder()
            .groupCode("GROUP001")
            .classroomName("A101")
            .slot(1)
            .day(DayOfWeek.MONDAY)
            .year("2024")
            .period("1")
            .build();

    when(groupService.addSession(sessionDto)).thenReturn(createdSession);

    ResponseEntity<Session> response = groupController.createSession(sessionDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("GROUP001", response.getBody().getGroupCode());
    verify(groupService, times(1)).addSession(sessionDto);
  }

  @Test
  void addStudent_WithValidData_ShouldReturnGroup() {
    String groupCode = "GROUP001";
    String studentId = "STU001";
    Group updatedGroup =
        new Group.GroupBuilder()
            .groupCode(groupCode)
            .abbreviation("MATH101")
            .year("2024")
            .period("1")
            .professorId("PROF001")
            .isLab(false)
            .groupNum("01")
            .enrolled(31)
            .maxCapacity(40)
            .build();

    when(groupService.addStudent(groupCode, studentId)).thenReturn(updatedGroup);

    ResponseEntity<Group> response = groupController.addStudent(groupCode, studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(31, response.getBody().getEnrolled());
    verify(groupService, times(1)).addStudent(groupCode, studentId);
  }

  @Test
  void removeStudent_WithValidData_ShouldReturnGroup() {
    String groupCode = "GROUP001";
    String studentId = "STU001";
    Group updatedGroup =
        new Group.GroupBuilder()
            .groupCode(groupCode)
            .abbreviation("MATH101")
            .year("2024")
            .period("1")
            .professorId("PROF001")
            .isLab(false)
            .groupNum("01")
            .enrolled(29)
            .maxCapacity(40)
            .build();

    when(groupService.deleteStudent(groupCode, studentId)).thenReturn(updatedGroup);

    ResponseEntity<Group> response = groupController.removeStudent(groupCode, studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(29, response.getBody().getEnrolled());
    verify(groupService, times(1)).deleteStudent(groupCode, studentId);
  }
}
