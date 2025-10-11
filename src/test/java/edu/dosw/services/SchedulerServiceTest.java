package edu.dosw.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchedulerServiceTest {

  private SessionService sessionService;
  private HistorialService historialService;
  private PeriodService periodService;
  private SchedulerService schedulerService;

  @BeforeEach
  void setUp() {
    sessionService = mock(SessionService.class);
    historialService = mock(HistorialService.class);
    periodService = mock(PeriodService.class);
    schedulerService = new SchedulerService(sessionService, historialService, periodService);
  }

//  @Test
//  void getScheduleById_shouldReturnScheduleWithSessions() {
//    // Arrange
//    String studentId = "S1";
//    String year = "2025";
//    String period = "1";
//    ArrayList<String> groupCodes = new ArrayList<>(List.of("G1", "G2"));
//
//    Session session1 = new Session("G1", "RoomA", 1, DayOfWeek.MONDAY, 2025, 1);
//    Session session2 = new Session("G2", "RoomB", 2, DayOfWeek.TUESDAY, 2025, 1);
//
//    when(periodService.getYear()).thenReturn(year);
//    when(periodService.getPeriod()).thenReturn(period);
//    when(historialService.getCurrentSessionsByStudentIdAndPeriod(studentId, year, period))
//        .thenReturn(groupCodes);
//    when(sessionService.getSessionsByGroupCode("G1"))
//        .thenReturn(new ArrayList<>(List.of(session1)));
//    when(sessionService.getSessionsByGroupCode("G2"))
//        .thenReturn(new ArrayList<>(List.of(session2)));
//
//    // Act
//    Schedule result = schedulerService.getScheduleById(studentId);
//
//    // Assert
//    assertThat(result).isNotNull();
//    assertThat(result.getStudentId()).isEqualTo("S1");
//    assertThat(result.getSessions()).hasSize(2);
//    assertThat(result.getSessions())
//        .extracting(Session::getGroupCode)
//        .containsExactlyInAnyOrder("G1", "G2");
//
//    verify(periodService).getYear();
//    verify(periodService).getPeriod();
//    verify(historialService).getCurrentSessionsByStudentIdAndPeriod(studentId, year, period);
//    verify(sessionService).getSessionsByGroupCode("G1");
//    verify(sessionService).getSessionsByGroupCode("G2");
//  }

  @Test
  void getScheduleById_shouldReturnEmptyScheduleWhenNoGroups() {
    // Arrange
    String studentId = "S1";
    String year = "2025";
    String period = "2";

    when(periodService.getYear()).thenReturn(year);
    when(periodService.getPeriod()).thenReturn(period);
    when(historialService.getCurrentSessionsByStudentIdAndPeriod(studentId, year, period))
        .thenReturn(new ArrayList<>());

    // Act
    Schedule result = schedulerService.getScheduleById(studentId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStudentId()).isEqualTo("S1");
    assertThat(result.getSessions()).isEmpty();

    verify(periodService).getYear();
    verify(periodService).getPeriod();
    verify(historialService).getCurrentSessionsByStudentIdAndPeriod(studentId, year, period);
    verifyNoInteractions(sessionService);
  }
}
