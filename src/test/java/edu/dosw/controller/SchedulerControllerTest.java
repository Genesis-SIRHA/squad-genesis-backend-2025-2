package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import edu.dosw.services.SchedulerService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SchedulerControllerTest {

  @Mock private SchedulerService schedulerService;

  @InjectMocks private SchedulerController schedulerController;

  private Schedule currentSchedule;
  private Schedule pastSchedule;
  private List<Schedule> pastSchedules;

  @BeforeEach
  void setUp() {
    Session session1 = new Session();
    session1.setGroupCode("MAT101");
    session1.setDay(DayOfWeek.MONDAY);
    session1.setSlot(1);
    session1.setYear("2024");
    session1.setPeriod("1");

    Session session2 = new Session();
    session2.setGroupCode("FIS201");
    session2.setDay(DayOfWeek.TUESDAY);
    session2.setSlot(2);
    session2.setYear("2023");
    session2.setPeriod("2");

    ArrayList<Session> currentSessions = new ArrayList<>();
    currentSessions.add(session1);
    currentSchedule = new Schedule("123", currentSessions);

    ArrayList<Session> pastSessionsList = new ArrayList<>();
    pastSessionsList.add(session2);
    pastSchedule = new Schedule("123", pastSessionsList);

    pastSchedules = new ArrayList<>();
    pastSchedules.add(pastSchedule);
  }

  @Test
  void getScheduleById_ShouldReturnActualScheduleStudent() {
    String studentId = "123";
    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(currentSchedule);

    ResponseEntity<Schedule> response = schedulerController.getActualScheduleByStudentId(studentId);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(currentSchedule, response.getBody());
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }

  @Test
  void getScheduleByPeriod_ShouldReturnSchedule() {
    String studentId = "123";
    String year = "2023";
    String period = "2";
    when(schedulerService.getScheduleByPeriod(studentId, year, period)).thenReturn(pastSchedule);

    ResponseEntity<Schedule> response =
        schedulerController.getScheduleByPeriod(studentId, year, period);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(pastSchedule, response.getBody());
    verify(schedulerService, times(1)).getScheduleByPeriod(studentId, year, period);
  }

  @Test
  void getPastSchedules_ShouldReturnPastSchedulesList() {
    String studentId = "123";
    when(schedulerService.getPastSchedules(studentId)).thenReturn(pastSchedules);

    ResponseEntity<List<Schedule>> response = schedulerController.getPastSchedules(studentId);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(pastSchedule, response.getBody().get(0));
    verify(schedulerService, times(1)).getPastSchedules(studentId);
  }

  @Test
  void getPastSchedules_WhenEmpty_ShouldReturnEmptyList() {
    String studentId = "123";
    when(schedulerService.getPastSchedules(studentId)).thenReturn(new ArrayList<>());

    ResponseEntity<List<Schedule>> response = schedulerController.getPastSchedules(studentId);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(schedulerService, times(1)).getPastSchedules(studentId);
  }
}
