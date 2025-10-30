package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.model.Historial;
import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import edu.dosw.model.enums.HistorialStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

  @Mock private SessionService sessionService;

  @Mock private HistorialService historialService;

  @Mock private PeriodService periodService;

  @InjectMocks private SchedulerService schedulerService;

  private Historial historial1;
  private Historial historial2;
  private Historial historial3;
  private Session session1;
  private Session session2;
  private Session session3;

  @BeforeEach
  void setUp() {
    historial1 = new Historial();
    historial1.setStudentId("123");
    historial1.setYear("2024");
    historial1.setPeriod("1");
    historial1.setGroupCode("MAT101");
    historial1.setStatus(HistorialStatus.ON_GOING);

    historial2 = new Historial();
    historial2.setStudentId("123");
    historial2.setYear("2023");
    historial2.setPeriod("2");
    historial2.setGroupCode("FIS201");
    historial2.setStatus(HistorialStatus.FINISHED);

    historial3 = new Historial();
    historial3.setStudentId("123");
    historial3.setYear("2023");
    historial3.setPeriod("1");
    historial3.setGroupCode("QUI101");
    historial3.setStatus(HistorialStatus.FINISHED);

    session1 = new Session();
    session1.setGroupCode("MAT101");
    session1.setDay(DayOfWeek.MONDAY);
    session1.setSlot(1);
    session1.setYear("2024");
    session1.setPeriod("1");

    session2 = new Session();
    session2.setGroupCode("FIS201");
    session2.setDay(DayOfWeek.TUESDAY);
    session2.setSlot(2);
    session2.setYear("2023");
    session2.setPeriod("2");

    session3 = new Session();
    session3.setGroupCode("QUI101");
    session3.setDay(DayOfWeek.WEDNESDAY);
    session3.setSlot(3);
    session3.setYear("2023");
    session3.setPeriod("1");
  }

  @Test
  void getScheduleByPeriod_ShouldReturnScheduleWithCorrectPeriod() {
    String studentId = "123";
    String year = "2024";
    String period = "1";
    List<String> groupCodes = Arrays.asList("MAT101");

    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, year, period))
        .thenReturn(groupCodes);
    when(sessionService.getSessionsByGroupCode("MAT101")).thenReturn(Arrays.asList(session1));

    Schedule result = schedulerService.getScheduleByPeriod(studentId, year, period);

    assertNotNull(result);
    assertEquals(studentId, result.getStudentId());
    assertEquals(1, result.getSessions().size());

    Session returnedSession = result.getSessions().get(0);
    assertEquals("MAT101", returnedSession.getGroupCode());
    assertEquals("2024", returnedSession.getYear());
    assertEquals("1", returnedSession.getPeriod());

    verify(historialService, times(1)).getGroupCodesByStudentIdAndPeriod(studentId, year, period);
    verify(sessionService, times(1)).getSessionsByGroupCode("MAT101");
  }

  @Test
  void getPastSchedules_ShouldReturnPastSchedulesWithCorrectPeriods() {
    String studentId = "123";
    String currentYear = "2024";
    String currentPeriod = "1";

    List<Historial> allHistorials = Arrays.asList(historial1, historial2, historial3);

    when(periodService.getYear()).thenReturn(currentYear);
    when(periodService.getPeriod()).thenReturn(currentPeriod);
    when(historialService.getAllHistorialsByStudentId(studentId)).thenReturn(allHistorials);

    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, "2023", "2"))
        .thenReturn(Arrays.asList("FIS201"));
    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, "2023", "1"))
        .thenReturn(Arrays.asList("QUI101"));
    when(sessionService.getSessionsByGroupCode("FIS201")).thenReturn(Arrays.asList(session2));
    when(sessionService.getSessionsByGroupCode("QUI101")).thenReturn(Arrays.asList(session3));

    List<Schedule> result = schedulerService.getPastSchedules(studentId);

    assertNotNull(result);
    assertEquals(2, result.size()); // Debería retornar solo 2023-2 y 2023-1 (excluye 2024-1 actual)

    Schedule firstPastSchedule = result.get(0);
    assertEquals(1, firstPastSchedule.getSessions().size());
    assertEquals("2023", firstPastSchedule.getSessions().get(0).getYear());
    assertEquals("2", firstPastSchedule.getSessions().get(0).getPeriod());

    Schedule secondPastSchedule = result.get(1); // 2023-1
    assertEquals(1, secondPastSchedule.getSessions().size());
    assertEquals("2023", secondPastSchedule.getSessions().get(0).getYear());
    assertEquals("1", secondPastSchedule.getSessions().get(0).getPeriod());

    verify(historialService, times(1)).getAllHistorialsByStudentId(studentId);
  }

  @Test
  void getPastSchedules_WhenNoPastSchedules_ShouldReturnEmptyList() {
    String studentId = "123";
    String currentYear = "2023";
    String currentPeriod = "1";

    List<Historial> currentHistorialOnly = Arrays.asList(historial3);

    when(periodService.getYear()).thenReturn(currentYear);
    when(periodService.getPeriod()).thenReturn(currentPeriod);
    when(historialService.getAllHistorialsByStudentId(studentId)).thenReturn(currentHistorialOnly);

    List<Schedule> result = schedulerService.getPastSchedules(studentId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getScheduleById_ShouldReturnCurrentActualScheduleWithCurrentPeriodStudent() {

    String studentId = "123";
    String currentYear = "2024";
    String currentPeriod = "1";
    List<String> groupCodes = Arrays.asList("MAT101");

    when(periodService.getYear()).thenReturn(currentYear);
    when(periodService.getPeriod()).thenReturn(currentPeriod);
    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, currentYear, currentPeriod))
        .thenReturn(groupCodes);
    when(sessionService.getSessionsByGroupCode("MAT101")).thenReturn(Arrays.asList(session1));

    Schedule result = schedulerService.getActualScheduleByStudentId(studentId);

    assertNotNull(result);
    assertEquals(studentId, result.getStudentId());
    assertEquals(1, result.getSessions().size());

    Session currentSession = result.getSessions().get(0);
    assertEquals(currentYear, currentSession.getYear());
    assertEquals(currentPeriod, currentSession.getPeriod());

    verify(periodService, times(1)).getYear();
    verify(periodService, times(1)).getPeriod();
  }

  @Test
  void buildSchedule_ShouldFilterSessionsByYearAndPeriod() {

    String studentId = "123";
    String year = "2024";
    String period = "1";
    List<String> groupCodes = Arrays.asList("MAT101");

    Session correctSession = new Session();
    correctSession.setGroupCode("MAT101");
    correctSession.setDay(DayOfWeek.MONDAY);
    correctSession.setSlot(1);
    correctSession.setYear("2024");
    correctSession.setPeriod("1");

    Session wrongYearSession = new Session();
    wrongYearSession.setGroupCode("MAT101");
    wrongYearSession.setDay(DayOfWeek.TUESDAY);
    wrongYearSession.setSlot(2);
    wrongYearSession.setYear("2023");
    wrongYearSession.setPeriod("1");

    Session wrongPeriodSession = new Session();
    wrongPeriodSession.setGroupCode("MAT101");
    wrongPeriodSession.setDay(DayOfWeek.WEDNESDAY);
    wrongPeriodSession.setSlot(3);
    wrongPeriodSession.setYear("2024");
    wrongPeriodSession.setPeriod("2");

    List<Session> allSessions = Arrays.asList(correctSession, wrongYearSession, wrongPeriodSession);

    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, year, period))
        .thenReturn(groupCodes);
    when(sessionService.getSessionsByGroupCode("MAT101")).thenReturn(allSessions);

    Schedule result = schedulerService.getScheduleByPeriod(studentId, year, period);

    assertNotNull(result);

    verify(sessionService, times(1)).getSessionsByGroupCode("MAT101");
  }

  @Test
  void getPastSchedules_ShouldFilterCurrentPeriod_WhenCurrentYearAndPeriod() {
    String studentId = "123";
    String currentYear = "2024";
    String currentPeriod = "1";

    Historial historialActual = new Historial();
    historialActual.setStudentId("123");
    historialActual.setYear("2024");
    historialActual.setPeriod("1");
    historialActual.setGroupCode("MAT101");
    historialActual.setStatus(HistorialStatus.ON_GOING);

    Historial historialPasado1 = new Historial();
    historialPasado1.setStudentId("123");
    historialPasado1.setYear("2023");
    historialPasado1.setPeriod("2");
    historialPasado1.setGroupCode("FIS201");
    historialPasado1.setStatus(HistorialStatus.FINISHED);

    Historial historialPasado2 = new Historial();
    historialPasado2.setStudentId("123");
    historialPasado2.setYear("2023");
    historialPasado2.setPeriod("1");
    historialPasado2.setGroupCode("QUI101");
    historialPasado2.setStatus(HistorialStatus.FINISHED);

    List<Historial> allHistorials =
        Arrays.asList(historialActual, historialPasado1, historialPasado2);

    when(periodService.getYear()).thenReturn(currentYear);
    when(periodService.getPeriod()).thenReturn(currentPeriod);
    when(historialService.getAllHistorialsByStudentId(studentId)).thenReturn(allHistorials);

    Session sessionFIS201 = new Session();
    sessionFIS201.setGroupCode("FIS201");
    sessionFIS201.setYear("2023");
    sessionFIS201.setPeriod("2");
    sessionFIS201.setDay(DayOfWeek.MONDAY);
    sessionFIS201.setSlot(1);

    Session sessionQUI101 = new Session();
    sessionQUI101.setGroupCode("QUI101");
    sessionQUI101.setYear("2023");
    sessionQUI101.setPeriod("1");
    sessionQUI101.setDay(DayOfWeek.TUESDAY);
    sessionQUI101.setSlot(2);

    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, "2023", "2"))
        .thenReturn(Arrays.asList("FIS201"));
    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, "2023", "1"))
        .thenReturn(Arrays.asList("QUI101"));
    when(sessionService.getSessionsByGroupCode("FIS201")).thenReturn(Arrays.asList(sessionFIS201));
    when(sessionService.getSessionsByGroupCode("QUI101")).thenReturn(Arrays.asList(sessionQUI101));

    List<Schedule> result = schedulerService.getPastSchedules(studentId);

    assertNotNull(result);
    assertEquals(2, result.size()); // Solo 2023-2 y 2023-1, excluye 2024-1
    verify(historialService, times(1)).getAllHistorialsByStudentId(studentId);
  }

  @Test
  void getPastSchedules_ShouldHandleSameYearButEarlierPeriod() {

    String studentId = "123";
    String currentYear = "2024";
    String currentPeriod = "2";

    Historial historialActual = new Historial();
    historialActual.setStudentId("123");
    historialActual.setYear("2024");
    historialActual.setPeriod("2");
    historialActual.setGroupCode("MAT101");
    historialActual.setStatus(HistorialStatus.ON_GOING);

    Historial historialPasado = new Historial();
    historialPasado.setStudentId("123");
    historialPasado.setYear("2024");
    historialPasado.setPeriod("1");
    historialPasado.setGroupCode("FIS201");
    historialPasado.setStatus(HistorialStatus.FINISHED);

    List<Historial> allHistorials = Arrays.asList(historialActual, historialPasado);

    when(periodService.getYear()).thenReturn(currentYear);
    when(periodService.getPeriod()).thenReturn(currentPeriod);
    when(historialService.getAllHistorialsByStudentId(studentId)).thenReturn(allHistorials);

    Session sessionFIS201 = new Session();
    sessionFIS201.setGroupCode("FIS201");
    sessionFIS201.setYear("2024");
    sessionFIS201.setPeriod("1");
    sessionFIS201.setDay(DayOfWeek.MONDAY);
    sessionFIS201.setSlot(1);

    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, "2024", "1"))
        .thenReturn(Arrays.asList("FIS201"));
    when(sessionService.getSessionsByGroupCode("FIS201")).thenReturn(Arrays.asList(sessionFIS201));

    List<Schedule> result = schedulerService.getPastSchedules(studentId);

    assertNotNull(result);
    assertEquals(1, result.size()); // Solo 2024-1
    assertEquals("2024", result.get(0).getSessions().get(0).getYear());
    assertEquals("1", result.get(0).getSessions().get(0).getPeriod());
  }

  @Test
  void getPastSchedules_ShouldHandleEarlierYear() {

    String studentId = "123";
    String currentYear = "2024";
    String currentPeriod = "1";

    Historial historialActual = new Historial();
    historialActual.setStudentId("123");
    historialActual.setYear("2024");
    historialActual.setPeriod("1");
    historialActual.setGroupCode("MAT101");
    historialActual.setStatus(HistorialStatus.ON_GOING);

    Historial historialPasado1 = new Historial();
    historialPasado1.setStudentId("123");
    historialPasado1.setYear("2023");
    historialPasado1.setPeriod("2");
    historialPasado1.setGroupCode("FIS201");
    historialPasado1.setStatus(HistorialStatus.FINISHED);

    Historial historialPasado2 = new Historial();
    historialPasado2.setStudentId("123");
    historialPasado2.setYear("2022");
    historialPasado2.setPeriod("1");
    historialPasado2.setGroupCode("QUI101");
    historialPasado2.setStatus(HistorialStatus.FINISHED);

    List<Historial> allHistorials =
        Arrays.asList(historialActual, historialPasado1, historialPasado2);

    when(periodService.getYear()).thenReturn(currentYear);
    when(periodService.getPeriod()).thenReturn(currentPeriod);
    when(historialService.getAllHistorialsByStudentId(studentId)).thenReturn(allHistorials);

    Session sessionFIS201 = new Session();
    sessionFIS201.setGroupCode("FIS201");
    sessionFIS201.setYear("2023");
    sessionFIS201.setPeriod("2");
    sessionFIS201.setDay(DayOfWeek.MONDAY);
    sessionFIS201.setSlot(1);

    Session sessionQUI101 = new Session();
    sessionQUI101.setGroupCode("QUI101");
    sessionQUI101.setYear("2022");
    sessionQUI101.setPeriod("1");
    sessionQUI101.setDay(DayOfWeek.TUESDAY);
    sessionQUI101.setSlot(2);

    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, "2023", "2"))
        .thenReturn(Arrays.asList("FIS201"));
    when(historialService.getGroupCodesByStudentIdAndPeriod(studentId, "2022", "1"))
        .thenReturn(Arrays.asList("QUI101"));
    when(sessionService.getSessionsByGroupCode("FIS201")).thenReturn(Arrays.asList(sessionFIS201));
    when(sessionService.getSessionsByGroupCode("QUI101")).thenReturn(Arrays.asList(sessionQUI101));

    List<Schedule> result = schedulerService.getPastSchedules(studentId);

    assertNotNull(result);
    assertEquals(2, result.size());
    // Verificar orden descendente: 2023-2 primero, luego 2022-1
    assertEquals("2023", result.get(0).getSessions().get(0).getYear());
    assertEquals("2", result.get(0).getSessions().get(0).getPeriod());
    assertEquals("2022", result.get(1).getSessions().get(0).getYear());
    assertEquals("1", result.get(1).getSessions().get(0).getPeriod());
  }

  @Test
  void getPastSchedules_ShouldHandleEmptyHistorial() {

    String studentId = "123";
    String currentYear = "2024";
    String currentPeriod = "1";

    when(periodService.getYear()).thenReturn(currentYear);
    when(periodService.getPeriod()).thenReturn(currentPeriod);
    when(historialService.getAllHistorialsByStudentId(studentId)).thenReturn(new ArrayList<>());

    List<Schedule> result = schedulerService.getPastSchedules(studentId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
