package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.ReportDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.services.StatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

  @Mock private StatsService statsService;

  @InjectMocks private StatsController statsController;

  @Test
  void getCourseReassignmentStats_ShouldReturnReportDTO() {
    ReportDTO expectedReport = new ReportDTO(50, 10, 30, 10, 20, 25, 25);
    when(statsService.getCourseReassignmentStats("MATH101")).thenReturn(expectedReport);

    ResponseEntity<ReportDTO> response = statsController.getCourseReassignmentStats("MATH101");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expectedReport, response.getBody());
    assertEquals(50, response.getBody().total());
    assertEquals(10, response.getBody().pending());
    verify(statsService, times(1)).getCourseReassignmentStats("MATH101");
  }

  @Test
  void getGroupReassignmentStats_ShouldReturnReportDTO() {
    ReportDTO expectedReport = new ReportDTO(40, 5, 30, 5, 10, 15, 5);
    when(statsService.getGroupReassignmentStats("GROUP001")).thenReturn(expectedReport);

    ResponseEntity<ReportDTO> response = statsController.getGroupReassignmentStats("GROUP001");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedReport, response.getBody());
    assertEquals(40, response.getBody().total());
    assertEquals(30, response.getBody().approved());
    verify(statsService, times(1)).getGroupReassignmentStats("GROUP001");
  }

  @Test
  void getFacultyReassignmentStats_ShouldReturnReportDTO() {
    ReportDTO expectedReport = new ReportDTO(15, 3, 10, 2, 4, 5, 1);
    when(statsService.getFacultyReassignmentStats("Engineering", "2024"))
        .thenReturn(expectedReport);

    ResponseEntity<ReportDTO> response =
        statsController.getFacultyReassignmentStats("Engineering", "2024");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedReport, response.getBody());
    assertEquals(15, response.getBody().total());
    assertEquals(10, response.getBody().approved());
    verify(statsService, times(1)).getFacultyReassignmentStats("Engineering", "2024");
  }

  @Test
  void getGlobalReassignmentStats_ShouldReturnReportDTO() {
    ReportDTO expectedReport = new ReportDTO(100, 20, 70, 10, 30, 40, 30);
    when(statsService.getGlobalReassignmentStats()).thenReturn(expectedReport);

    ResponseEntity<ReportDTO> response = statsController.getGlobalReassignmentStats();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedReport, response.getBody());
    assertEquals(100, response.getBody().total());
    assertEquals(70, response.getBody().approved());
    verify(statsService, times(1)).getGlobalReassignmentStats();
  }

  @Test
  void getRequestStats_ShouldReturnRequestStats() {
    RequestStats expectedStats = new RequestStats(100, 20, 70, 10);
    when(statsService.getRequestStats()).thenReturn(expectedStats);

    ResponseEntity<RequestStats> response = statsController.getRequestStats();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expectedStats, response.getBody());
    assertEquals(100, response.getBody().total());
    assertEquals(20, response.getBody().pending());
    verify(statsService, times(1)).getRequestStats();
  }

  @Test
  void getCourseReassignmentStats_WhenServiceReturnsEmpty_ShouldReturnEmptyReport() {
    ReportDTO emptyReport = new ReportDTO(0, 0, 0, 0, 0, 0, 0);
    when(statsService.getCourseReassignmentStats("UNKNOWN")).thenReturn(emptyReport);

    ResponseEntity<ReportDTO> response = statsController.getCourseReassignmentStats("UNKNOWN");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(emptyReport, response.getBody());
    assertEquals(0, response.getBody().total());
    assertEquals(0, response.getBody().approved());
  }

  @Test
  void getAllEndpoints_ShouldCallServiceExactlyOnce() {
    ReportDTO report = new ReportDTO(10, 2, 6, 2, 3, 4, 3);
    RequestStats stats = new RequestStats(50, 10, 30, 10);

    when(statsService.getCourseReassignmentStats("MATH101")).thenReturn(report);
    when(statsService.getGroupReassignmentStats("GROUP001")).thenReturn(report);
    when(statsService.getFacultyReassignmentStats("Engineering", "2024")).thenReturn(report);
    when(statsService.getGlobalReassignmentStats()).thenReturn(report);
    when(statsService.getRequestStats()).thenReturn(stats);

    statsController.getCourseReassignmentStats("MATH101");
    statsController.getGroupReassignmentStats("GROUP001");
    statsController.getFacultyReassignmentStats("Engineering", "2024");
    statsController.getGlobalReassignmentStats();
    statsController.getRequestStats();

    verify(statsService, times(1)).getCourseReassignmentStats("MATH101");
    verify(statsService, times(1)).getGroupReassignmentStats("GROUP001");
    verify(statsService, times(1)).getFacultyReassignmentStats("Engineering", "2024");
    verify(statsService, times(1)).getGlobalReassignmentStats();
    verify(statsService, times(1)).getRequestStats();
    verifyNoMoreInteractions(statsService);
  }

  @Test
  void getCourseReassignmentStats_WhenServiceThrowsException_ShouldPropagateException() {
    when(statsService.getCourseReassignmentStats("MATH101"))
        .thenThrow(new RuntimeException("Service error"));

    assertThrows(
        RuntimeException.class, () -> statsController.getCourseReassignmentStats("MATH101"));
    verify(statsService, times(1)).getCourseReassignmentStats("MATH101");
  }

  @Test
  void getFacultyReassignmentStats_WithDifferentPlan_ShouldUseCorrectPlan() {
    ReportDTO expectedReport = new ReportDTO(25, 5, 15, 5, 8, 10, 7);
    when(statsService.getFacultyReassignmentStats("Science", "2025B")).thenReturn(expectedReport);

    ResponseEntity<ReportDTO> response =
        statsController.getFacultyReassignmentStats("Science", "2025B");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedReport, response.getBody());
    assertEquals(25, response.getBody().total());
    assertEquals(15, response.getBody().approved());
    verify(statsService, times(1)).getFacultyReassignmentStats("Science", "2025B");
  }
}
