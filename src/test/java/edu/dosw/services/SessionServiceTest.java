package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.dosw.dto.SessionDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import edu.dosw.repositories.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

  @Mock private SessionRepository sessionRepository;

  @Mock private PeriodService periodService;

  @Mock private SessionValidator sessionValidator;

  @InjectMocks private SessionService sessionService;

  private Session testSession;
  private SessionDTO testSessionDTO;
  private final String TEST_SESSION_ID = UUID.randomUUID().toString();
  private final String TEST_GROUP_CODE = "GROUP_001";
  private final String TEST_CLASSROOM = "CLASSROOM_A";
  private final String TEST_YEAR = "2024";
  private final String TEST_PERIOD = "1";

  @BeforeEach
  void setUp() {
    testSession =
        new Session.SessionBuilder()
            .groupCode(TEST_GROUP_CODE)
            .classroomName(TEST_CLASSROOM)
            .slot(1)
            .day(DayOfWeek.MONDAY)
            .year(TEST_YEAR)
            .period(TEST_PERIOD)
            .build();
    testSession.setId("123");
    testSession.setSessionId(TEST_SESSION_ID);

    testSessionDTO = new SessionDTO(TEST_GROUP_CODE, TEST_CLASSROOM, 1, DayOfWeek.MONDAY);
  }

  @Test
  void getSessionsByGroupCode_WhenSessionsExist_ShouldReturnSessions() {
    List<Session> expectedSessions = List.of(testSession);
    when(sessionRepository.findByGroupCode(TEST_GROUP_CODE))
        .thenReturn(new ArrayList<>(expectedSessions));

    List<Session> result = sessionService.getSessionsByGroupCode(TEST_GROUP_CODE);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(TEST_GROUP_CODE, result.get(0).getGroupCode());
    verify(sessionRepository).findByGroupCode(TEST_GROUP_CODE);
  }

  @Test
  void getSessionsByGroupCode_WhenNoSessionsExist_ShouldThrowResourceNotFoundException() {
    when(sessionRepository.findByGroupCode(TEST_GROUP_CODE)).thenReturn(null);

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> sessionService.getSessionsByGroupCode(TEST_GROUP_CODE));

    assertTrue(
        exception
            .getMessage()
            .contains("Group schedule not found by GroupCode: " + TEST_GROUP_CODE));
    verify(sessionRepository).findByGroupCode(TEST_GROUP_CODE);
  }

  @Test
  void getSessionBySessionId_WhenSessionExists_ShouldReturnSession() {
    when(sessionRepository.findBySessionId(TEST_SESSION_ID)).thenReturn(testSession);

    Session result = sessionService.getSessionBySessionId(TEST_SESSION_ID);

    assertNotNull(result);
    assertEquals(TEST_SESSION_ID, result.getSessionId());
    assertEquals(TEST_GROUP_CODE, result.getGroupCode());
    verify(sessionRepository).findBySessionId(TEST_SESSION_ID);
  }

  @Test
  void getSessionBySessionId_WhenSessionNotExists_ShouldThrowResourceNotFoundException() {
    when(sessionRepository.findBySessionId(TEST_SESSION_ID)).thenReturn(null);

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> sessionService.getSessionBySessionId(TEST_SESSION_ID));

    assertTrue(
        exception.getMessage().contains("Session not found by sessionId: " + TEST_SESSION_ID));
    verify(sessionRepository).findBySessionId(TEST_SESSION_ID);
  }

  @Test
  void getSessionsByScheduleAndClassroom_WhenSessionExists_ShouldReturnSession() {
    when(periodService.getYear()).thenReturn(TEST_YEAR);
    when(periodService.getPeriod()).thenReturn(TEST_PERIOD);
    when(sessionRepository.getSessionByScheduleAndClassroom(
            TEST_CLASSROOM, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD))
        .thenReturn(testSession);

    Session result = sessionService.getSessionsByScheduleAndClassroom(testSessionDTO);

    assertNotNull(result);
    assertEquals(TEST_CLASSROOM, result.getClassroomName());
    assertEquals(DayOfWeek.MONDAY, result.getDay());
    verify(sessionRepository)
        .getSessionByScheduleAndClassroom(
            TEST_CLASSROOM, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD);
  }

  @Test
  void
      getSessionsByScheduleAndClassroom_WhenSessionNotExists_ShouldThrowResourceNotFoundException() {
    when(periodService.getYear()).thenReturn(TEST_YEAR);
    when(periodService.getPeriod()).thenReturn(TEST_PERIOD);
    when(sessionRepository.getSessionByScheduleAndClassroom(
            TEST_CLASSROOM, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD))
        .thenReturn(null);

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> sessionService.getSessionsByScheduleAndClassroom(testSessionDTO));

    assertTrue(
        exception.getMessage().contains("Session not found by sessionId: " + DayOfWeek.MONDAY));
    verify(sessionRepository)
        .getSessionByScheduleAndClassroom(
            TEST_CLASSROOM, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD);
  }

  @Test
  void getSessionsByScheduleAndGroupCode_WhenSessionExists_ShouldReturnSession() {
    when(periodService.getYear()).thenReturn(TEST_YEAR);
    when(periodService.getPeriod()).thenReturn(TEST_PERIOD);
    when(sessionRepository.getSessionByScheduleAndGroupCode(
            TEST_GROUP_CODE, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD))
        .thenReturn(testSession);

    Session result = sessionService.getSessionsByScheduleAndGroupCode(testSessionDTO);

    assertNotNull(result);
    assertEquals(TEST_GROUP_CODE, result.getGroupCode());
    assertEquals(1, result.getSlot());
    verify(sessionRepository)
        .getSessionByScheduleAndGroupCode(
            TEST_GROUP_CODE, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD);
  }

  @Test
  void
      getSessionsByScheduleAndGroupCode_WhenSessionNotExists_ShouldThrowResourceNotFoundException() {
    when(periodService.getYear()).thenReturn(TEST_YEAR);
    when(periodService.getPeriod()).thenReturn(TEST_PERIOD);
    when(sessionRepository.getSessionByScheduleAndGroupCode(
            TEST_GROUP_CODE, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD))
        .thenReturn(null);

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> sessionService.getSessionsByScheduleAndGroupCode(testSessionDTO));

    assertTrue(
        exception.getMessage().contains("Session not found by sessionId: " + DayOfWeek.MONDAY));
    verify(sessionRepository)
        .getSessionByScheduleAndGroupCode(
            TEST_GROUP_CODE, 1, DayOfWeek.MONDAY, TEST_YEAR, TEST_PERIOD);
  }

  @Test
  void createSession_WithValidData_ShouldReturnCreatedSession() {
    when(periodService.getYear()).thenReturn(TEST_YEAR);
    when(periodService.getPeriod()).thenReturn(TEST_PERIOD);
    when(sessionRepository.save(any(Session.class))).thenReturn(testSession);
    doNothing().when(sessionValidator).validateCreateSession(testSessionDTO);

    Session result = sessionService.createSession(testSessionDTO);

    assertNotNull(result);
    verify(sessionValidator).validateCreateSession(testSessionDTO);
    verify(sessionRepository).save(any(Session.class));
  }

  @Test
  void createSession_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
    when(periodService.getYear()).thenReturn(TEST_YEAR);
    when(periodService.getPeriod()).thenReturn(TEST_PERIOD);
    doNothing().when(sessionValidator).validateCreateSession(testSessionDTO);
    when(sessionRepository.save(any(Session.class)))
        .thenThrow(new RuntimeException("Database error"));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> sessionService.createSession(testSessionDTO));

    assertTrue(exception.getMessage().contains("Failed to create session: Database error"));
    verify(sessionRepository).save(any(Session.class));
  }

  @Test
  void updateSession_WithValidData_ShouldReturnUpdatedSession() {
    SessionDTO updateDTO =
        new SessionDTO("UPDATED_GROUP", "UPDATED_CLASSROOM", 2, DayOfWeek.TUESDAY);

    when(sessionRepository.findBySessionId(TEST_SESSION_ID)).thenReturn(testSession);
    doNothing().when(sessionValidator).validateUpdateSession(updateDTO, TEST_YEAR, TEST_PERIOD);
    when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

    Session result = sessionService.updateSession(TEST_SESSION_ID, updateDTO);

    assertNotNull(result);
    verify(sessionValidator).validateUpdateSession(updateDTO, TEST_YEAR, TEST_PERIOD);
    verify(sessionRepository).save(testSession);
  }

  @Test
  void updateSession_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
    SessionDTO updateDTO =
        new SessionDTO("UPDATED_GROUP", "UPDATED_CLASSROOM", 2, DayOfWeek.TUESDAY);

    when(sessionRepository.findBySessionId(TEST_SESSION_ID)).thenReturn(testSession);
    doNothing().when(sessionValidator).validateUpdateSession(updateDTO, TEST_YEAR, TEST_PERIOD);
    when(sessionRepository.save(any(Session.class)))
        .thenThrow(new RuntimeException("Update error"));

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> sessionService.updateSession(TEST_SESSION_ID, updateDTO));

    assertTrue(
        exception.getMessage().contains("An unexpected error has occurred updating a session:"));
    verify(sessionRepository).save(testSession);
  }

  @Test
  void deleteSession_WithValidSession_ShouldDeleteAndReturnSession() {
    when(sessionRepository.findBySessionId(TEST_SESSION_ID)).thenReturn(testSession);
    doNothing().when(sessionValidator).validateDeleteSession(testSession);
    doNothing().when(sessionRepository).delete(testSession);

    Session result = sessionService.deleteSession(TEST_SESSION_ID);

    assertNotNull(result);
    assertEquals(TEST_SESSION_ID, result.getSessionId());
    verify(sessionValidator).validateDeleteSession(testSession);
    verify(sessionRepository).delete(testSession);
  }

  @Test
  void deleteSession_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
    when(sessionRepository.findBySessionId(TEST_SESSION_ID)).thenReturn(testSession);
    doNothing().when(sessionValidator).validateDeleteSession(testSession);
    doThrow(new RuntimeException("Delete error")).when(sessionRepository).delete(testSession);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> sessionService.deleteSession(TEST_SESSION_ID));

    assertTrue(
        exception.getMessage().contains("An unexpected error has occurred updating a session:"));
    verify(sessionRepository).delete(testSession);
  }

  @Test
  void deleteSessionsByGroupCode_WithExistingSessions_ShouldDeleteAllSessions() {
    List<Session> sessions = List.of(testSession);
    when(sessionRepository.findByGroupCode(TEST_GROUP_CODE)).thenReturn(new ArrayList<>(sessions));

    sessionService.deleteSessionsByGroupCode(TEST_GROUP_CODE);

    verify(sessionRepository).findByGroupCode(TEST_GROUP_CODE);
    verify(sessionRepository, times(sessions.size())).delete(any(Session.class));
  }

  @Test
  void deleteSessionsByGroupCode_WithNoSessions_ShouldNotDeleteAnything() {
    when(sessionRepository.findByGroupCode(TEST_GROUP_CODE)).thenReturn(new ArrayList<>());

    sessionService.deleteSessionsByGroupCode(TEST_GROUP_CODE);

    verify(sessionRepository).findByGroupCode(TEST_GROUP_CODE);
    verify(sessionRepository, never()).delete(any(Session.class));
  }

  @Test
  void updateSession_WithPartialData_ShouldUpdateOnlyProvidedFields() {
    SessionDTO partialUpdateDTO =
        new SessionDTO(null, "UPDATED_CLASSROOM", null, DayOfWeek.TUESDAY);

    when(sessionRepository.findBySessionId(TEST_SESSION_ID)).thenReturn(testSession);
    doNothing()
        .when(sessionValidator)
        .validateUpdateSession(partialUpdateDTO, TEST_YEAR, TEST_PERIOD);
    when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

    String originalGroupCode = testSession.getGroupCode();
    int originalSlot = testSession.getSlot();

    Session result = sessionService.updateSession(TEST_SESSION_ID, partialUpdateDTO);

    assertNotNull(result);
    assertEquals("UPDATED_CLASSROOM", result.getClassroomName());
    assertEquals(DayOfWeek.TUESDAY, result.getDay());
    assertEquals(originalGroupCode, result.getGroupCode());
    assertEquals(originalSlot, result.getSlot());
    verify(sessionRepository).save(testSession);
  }
}
