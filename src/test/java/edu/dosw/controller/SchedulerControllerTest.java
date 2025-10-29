package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.model.Schedule;
import edu.dosw.services.SchedulerService;
import java.util.ArrayList;
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

  @Test
  void getScheduleById_WhenScheduleExists_ShouldReturnSchedule() {
    String studentId = "STU001";
    Schedule expectedSchedule = new Schedule(studentId, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(expectedSchedule);

    ResponseEntity<Schedule> response = schedulerController.getActualScheduleByStudentId(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().getStudentId());
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }

  @Test
  void getScheduleById_WhenScheduleNotExists_ShouldThrowException() {
    String studentId = "NONEXISTENT";
    when(schedulerService.getActualScheduleByStudentId(studentId))
        .thenThrow(new RuntimeException("Schedule not found"));

    assertThrows(
        RuntimeException.class, () -> schedulerController.getActualScheduleByStudentId(studentId));
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }

  @Test
  void getScheduleById_WithEmptySchedule_ShouldReturnEmptySchedule() {
    String studentId = "STU002";
    Schedule emptySchedule = new Schedule(studentId, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(emptySchedule);

    ResponseEntity<Schedule> response = schedulerController.getActualScheduleByStudentId(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().getStudentId());
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }

  @Test
  void getScheduleById_ShouldCallServiceWithCorrectStudentId() {
    String studentId = "STU003";
    Schedule schedule = new Schedule(studentId, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(schedule);

    schedulerController.getActualScheduleByStudentId(studentId);

    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }

  @Test
  void getScheduleById_WithMultipleCalls_ShouldCallServiceEachTime() {
    String studentId1 = "STU001";
    String studentId2 = "STU002";

    Schedule schedule1 = new Schedule(studentId1, new ArrayList<>());
    Schedule schedule2 = new Schedule(studentId2, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId1)).thenReturn(schedule1);
    when(schedulerService.getActualScheduleByStudentId(studentId2)).thenReturn(schedule2);

    ResponseEntity<Schedule> response1 =
        schedulerController.getActualScheduleByStudentId(studentId1);
    ResponseEntity<Schedule> response2 =
        schedulerController.getActualScheduleByStudentId(studentId2);

    assertEquals(HttpStatus.OK, response1.getStatusCode());
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    assertEquals(studentId1, response1.getBody().getStudentId());
    assertEquals(studentId2, response2.getBody().getStudentId());
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId1);
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId2);
  }

  @Test
  void getScheduleById_WhenServiceReturnsNull_ShouldReturnNullResponse() {
    String studentId = "STU004";
    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(null);

    ResponseEntity<Schedule> response = schedulerController.getActualScheduleByStudentId(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody());
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }

  @Test
  void getScheduleById_ShouldReturnCorrectResponseType() {
    String studentId = "STU005";
    Schedule schedule = new Schedule(studentId, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(schedule);

    ResponseEntity<Schedule> response = schedulerController.getActualScheduleByStudentId(studentId);

    assertInstanceOf(ResponseEntity.class, response);
    assertInstanceOf(Schedule.class, response.getBody());
  }

  @Test
  void getScheduleById_VerifyServiceInteraction() {
    String studentId = "STU006";
    Schedule schedule = new Schedule(studentId, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(schedule);

    schedulerController.getActualScheduleByStudentId(studentId);

    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
    verifyNoMoreInteractions(schedulerService);
  }

  @Test
  void getScheduleById_WithSpecialCharactersInStudentId_ShouldHandleCorrectly() {
    String studentId = "STU-007-ABC";
    Schedule schedule = new Schedule(studentId, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(schedule);

    ResponseEntity<Schedule> response = schedulerController.getActualScheduleByStudentId(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().getStudentId());
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }

  @Test
  void getScheduleById_WithLongStudentId_ShouldHandleCorrectly() {
    String studentId = "STU001234567890123456789";
    Schedule schedule = new Schedule(studentId, new ArrayList<>());

    when(schedulerService.getActualScheduleByStudentId(studentId)).thenReturn(schedule);

    ResponseEntity<Schedule> response = schedulerController.getActualScheduleByStudentId(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().getStudentId());
    verify(schedulerService, times(1)).getActualScheduleByStudentId(studentId);
  }
}
