package edu.dosw.services;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.HistorialDTO;
import edu.dosw.dto.SessionDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.Session;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.repositories.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private FacultyService facultyService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private PeriodService periodService;

    @Mock
    private SessionService sessionService;

    @Mock
    private HistorialService historialService;

    @Mock
    private GroupValidator groupValidator;

    @InjectMocks
    private GroupService groupService;

    @Test
    void getAllGroupsByCourseAbbreviation_ShouldReturnGroups() {
        List<Group> expectedGroups = Arrays.asList(createGroup("GROUP1"), createGroup("GROUP2"));
        when(groupRepository.findAllByCourseId("CS101")).thenReturn(expectedGroups);

        List<Group> result = groupService.getAllGroupsByCourseAbbreviation("CS101");

        assertEquals(2, result.size());
        verify(groupRepository).findAllByCourseId("CS101");
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

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> groupService.getGroupByGroupCode("NONEXISTENT"));

        assertEquals("Group not found: NONEXISTENT", exception.getMessage());
        verify(groupRepository).findByGroupCode("NONEXISTENT");
    }

    @Test
    void createGroup_WithValidData_ShouldReturnSavedGroup() {
        CreationGroupRequest request = new CreationGroupRequest("GROUP1", "CS101", "PROF123", false, "1", 30, 0);
        Course course = createCourse("CS101");
        Group group = createGroup("GROUP1");

        when(facultyService.findCourseByAbbreviation("CS101", "Engineering", "2024")).thenReturn(course);
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
        CreationGroupRequest request = new CreationGroupRequest("GROUP1", "CS101", "PROF123", false, "1", 30, 0);
        when(facultyService.findCourseByAbbreviation("CS101", "Engineering", "2024")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
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

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
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

        BusinessException exception = assertThrows(BusinessException.class,
                () -> groupService.deleteGroup("GROUP1"));

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
    void deleteStudent_WhenHistorialUpdateFails_ShouldThrowBusinessException() {
        Group group = createGroup("GROUP1");
        group.setEnrolled(1);
        group.setYear("2024");
        group.setPeriod("1");

        when(groupRepository.findByGroupCode("GROUP1")).thenReturn(Optional.of(group));
        when(periodService.getYear()).thenReturn("2024");
        when(periodService.getPeriod()).thenReturn("1");
        when(groupRepository.save(group)).thenReturn(group);
        doThrow(new RuntimeException("Historial error")).when(historialService).updateHistorial("STUD123", "GROUP1", HistorialStatus.CANCELLED);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> groupService.deleteStudent("GROUP1", "STUD123"));

        assertTrue(exception.getMessage().contains("Failed to update historial"));
    }

    private Group createGroup(String groupCode) {
        return new Group(
                "ID123",
                groupCode,
                "CS101",
                "2024",
                "1",
                "PROF123",
                false,
                "1",
                0,
                30
        );
    }

    private Course createCourse(String abbreviation) {
        Course course = new Course();
        course.setAbbreviation(abbreviation);
        return course;
    }
}