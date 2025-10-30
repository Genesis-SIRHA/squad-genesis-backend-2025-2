package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.SessionDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import edu.dosw.services.Validators.SessionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionValidatorTest {

  @Mock private PeriodService periodService;

  @InjectMocks private SessionValidator sessionValidator;

  @Test
  void validateCreateSession_WithValidSlot_ShouldNotThrowException() {
    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 5, DayOfWeek.MONDAY);

    assertDoesNotThrow(() -> sessionValidator.validateCreateSession(sessionDTO));
  }

  @Test
  void validateCreateSession_WithNullSlot_ShouldThrowBusinessException() {
    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", null, DayOfWeek.MONDAY);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> sessionValidator.validateCreateSession(sessionDTO));

    assertEquals("The slot cannot be null", exception.getMessage());
  }

  @Test
  void validateCreateSession_WithSlotZero_ShouldThrowBusinessException() {
    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 0, DayOfWeek.MONDAY);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> sessionValidator.validateCreateSession(sessionDTO));

    assertEquals("The slot is invalid", exception.getMessage());
  }

  @Test
  void validateCreateSession_WithSlotNegative_ShouldThrowBusinessException() {
    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", -1, DayOfWeek.MONDAY);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> sessionValidator.validateCreateSession(sessionDTO));

    assertEquals("The slot is invalid", exception.getMessage());
  }

  @Test
  void validateCreateSession_WithSlotSeven_ShouldNotThrowException() {
    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 7, DayOfWeek.MONDAY);

    assertDoesNotThrow(() -> sessionValidator.validateCreateSession(sessionDTO));
  }

  @Test
  void validateCreateSession_WithSlotEight_ShouldNotThrowException() {
    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 8, DayOfWeek.MONDAY);

    assertDoesNotThrow(() -> sessionValidator.validateCreateSession(sessionDTO));
  }

  @Test
  void validateCreateSession_WithSlotGreaterThanEight_ShouldThrowBusinessException() {
    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 9, DayOfWeek.MONDAY);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> sessionValidator.validateCreateSession(sessionDTO));

    assertEquals("The slot is invalid", exception.getMessage());
  }

  @Test
  void validateUpdateSession_WithValidData_ShouldNotThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 5, DayOfWeek.MONDAY);
    String year = "2024";
    String period = "1";

    assertDoesNotThrow(() -> sessionValidator.validateUpdateSession(sessionDTO, year, period));
  }

  @Test
  void validateUpdateSession_WithInvalidYear_ShouldThrowBusinessException() {
    when(periodService.getYear()).thenReturn("2024");

    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 5, DayOfWeek.MONDAY);
    String year = "2023";
    String period = "1";

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> sessionValidator.validateUpdateSession(sessionDTO, year, period));

    assertEquals("The year or period are invalid", exception.getMessage());
  }

  @Test
  void validateUpdateSession_WithNullSlot_ShouldNotThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", null, DayOfWeek.MONDAY);
    String year = "2024";
    String period = "1";

    assertDoesNotThrow(() -> sessionValidator.validateUpdateSession(sessionDTO, year, period));
  }

  @Test
  void validateUpdateSession_WithInvalidSlot_ShouldThrowBusinessException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    SessionDTO sessionDTO = new SessionDTO("GROUP101", "CLASSROOM_A", 0, DayOfWeek.MONDAY);
    String year = "2024";
    String period = "1";

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> sessionValidator.validateUpdateSession(sessionDTO, year, period));

    assertEquals("The slot is invalid", exception.getMessage());
  }

  @Test
  void validateDeleteSession_WithValidSession_ShouldNotThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    Session session = new Session();
    session.setYear("2024");
    session.setPeriod("1");

    assertDoesNotThrow(() -> sessionValidator.validateDeleteSession(session));
  }

  @Test
  void validateDeleteSession_WithInvalidYear_ShouldThrowBusinessException() {
    when(periodService.getYear()).thenReturn("2024");

    Session session = new Session();
    session.setYear("2023");
    session.setPeriod("1");

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> sessionValidator.validateDeleteSession(session));

    assertEquals("The year or period are invalid", exception.getMessage());
  }

  @Test
  void validateDeleteSession_WithBothInvalid_ShouldThrowBusinessException() {
    Session session = new Session();
    session.setYear("2023");
    session.setPeriod("2");

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> sessionValidator.validateDeleteSession(session));

    assertEquals("The year or period are invalid", exception.getMessage());
  }

  @Test
  void validateCreateSession_WithBoundaryValidSlots_ShouldNotThrowException() {
    SessionDTO slot1 = new SessionDTO("GROUP101", "CLASSROOM_A", 1, DayOfWeek.MONDAY);
    SessionDTO slot8 = new SessionDTO("GROUP101", "CLASSROOM_A", 8, DayOfWeek.MONDAY);

    assertDoesNotThrow(() -> sessionValidator.validateCreateSession(slot1));
    assertDoesNotThrow(() -> sessionValidator.validateCreateSession(slot8));
  }

  @Test
  void validateUpdateSession_WithBoundaryValidSlots_ShouldNotThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    SessionDTO slot1 = new SessionDTO("GROUP101", "CLASSROOM_A", 1, DayOfWeek.MONDAY);
    SessionDTO slot8 = new SessionDTO("GROUP101", "CLASSROOM_A", 8, DayOfWeek.MONDAY);
    String year = "2024";
    String period = "1";

    assertDoesNotThrow(() -> sessionValidator.validateUpdateSession(slot1, year, period));
    assertDoesNotThrow(() -> sessionValidator.validateUpdateSession(slot8, year, period));
  }

  @Test
  void validateCreateSession_WithValidDataAndDifferentDays_ShouldNotThrowException() {
    SessionDTO mondaySession = new SessionDTO("GROUP101", "CLASSROOM_A", 3, DayOfWeek.MONDAY);
    SessionDTO fridaySession = new SessionDTO("GROUP102", "CLASSROOM_B", 5, DayOfWeek.FRIDAY);

    assertDoesNotThrow(() -> sessionValidator.validateCreateSession(mondaySession));
    assertDoesNotThrow(() -> sessionValidator.validateCreateSession(fridaySession));
  }

  @Test
  void validateUpdateSession_WithValidDataAndDifferentClassrooms_ShouldNotThrowException() {
    when(periodService.getYear()).thenReturn("2024");
    when(periodService.getPeriod()).thenReturn("1");

    SessionDTO session1 = new SessionDTO("GROUP101", "CLASSROOM_A", 2, DayOfWeek.TUESDAY);
    SessionDTO session2 = new SessionDTO("GROUP102", "CLASSROOM_B", 4, DayOfWeek.WEDNESDAY);
    String year = "2024";
    String period = "1";

    assertDoesNotThrow(() -> sessionValidator.validateUpdateSession(session1, year, period));
    assertDoesNotThrow(() -> sessionValidator.validateUpdateSession(session2, year, period));
  }
}
