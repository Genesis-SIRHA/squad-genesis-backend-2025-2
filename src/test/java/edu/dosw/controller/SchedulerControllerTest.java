package edu.dosw.controller;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import edu.dosw.model.Schedule;
//import edu.dosw.model.Session;
//import edu.dosw.services.SchedulerService;
//import java.time.DayOfWeek;
//import java.util.ArrayList;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//class SchedulerControllerTest {
//
//  @Mock private SchedulerService schedulerService;
//
//  @InjectMocks private SchedulerController schedulerController;
//
//  private Schedule schedule;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//
//    Session session = new Session("G1", "Room 101", 1, DayOfWeek.MONDAY, 2024, 1);
//    ArrayList<Session> sessions = new ArrayList<>();
//    sessions.add(session);
//
//    schedule = new Schedule("student123", sessions);
//  }
//
//  @Test
//  void getScheduleById_ShouldReturnSchedule_WhenStudentExists() {
//    when(schedulerService.getScheduleById("student123")).thenReturn(schedule);
//
//    ResponseEntity<Schedule> response = schedulerController.getScheduleById("student123");
//
//    assertEquals(200, response.getStatusCodeValue());
//    assertNotNull(response.getBody());
//    assertEquals("student123", response.getBody().getStudentId());
//    assertEquals(1, response.getBody().getSessions().size());
//    assertEquals("G1", response.getBody().getSessions().get(0).getGroupCode());
//
//    verify(schedulerService, times(1)).getScheduleById("student123");
//  }
//
//  @Test
//  void getScheduleById_ShouldReturnEmptySchedule_WhenNoSessionsFound() {
//    Schedule emptySchedule = new Schedule("student456", new ArrayList<>());
//    when(schedulerService.getScheduleById("student456")).thenReturn(emptySchedule);
//
//    ResponseEntity<Schedule> response = schedulerController.getScheduleById("student456");
//
//    assertEquals(200, response.getStatusCodeValue());
//    assertNotNull(response.getBody());
//    assertEquals("student456", response.getBody().getStudentId());
//    assertTrue(response.getBody().getSessions().isEmpty());
//
//    verify(schedulerService, times(1)).getScheduleById("student456");
//  }
//}
