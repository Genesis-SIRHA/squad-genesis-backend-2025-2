package edu.dosw.controller;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    @Mock
    private StatsService statsService;

    @InjectMocks
    private StatsController statsController;

    @Test
    void getCourseReassignmentStats_ShouldReturnReportDTO() {
        ReportDTO expectedReport = new ReportDTO(50L, 10L, 30L, 10L, 20L, 25L, 25L);
        when(statsService.getCourseReassignmentStats("MATH101")).thenReturn(expectedReport);

        ResponseEntity<ReportDTO> response = statsController.getCourseReassignmentStats("MATH101");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedReport, response.getBody());
        assertEquals(50L, response.getBody().total());
        assertEquals(10L, response.getBody().pending());
        verify(statsService, times(1)).getCourseReassignmentStats("MATH101");
    }

    @Test
    void getGroupReassignmentStats_ShouldReturnReportDTO() {
        ReportDTO expectedReport = new ReportDTO(40L, 5L, 30L, 5L, 10L, 15L, 5L);
        when(statsService.getGroupReassignmentStats("GROUP001")).thenReturn(expectedReport);

        ResponseEntity<ReportDTO> response = statsController.getGroupReassignmentStats("GROUP001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReport, response.getBody());
        assertEquals(40L, response.getBody().total());
        assertEquals(30L, response.getBody().approved());
        verify(statsService, times(1)).getGroupReassignmentStats("GROUP001");
    }

    @Test
    void getFacultyReassignmentStats_ShouldReturnReportDTO() {
        ReportDTO expectedReport = new ReportDTO(15L, 3L, 10L, 2L, 4L, 5L, 1L);
        when(statsService.getFacultyReassignmentStats("Engineering", "2024")).thenReturn(expectedReport);

        ResponseEntity<ReportDTO> response = statsController.getFacultyReassignmentStats("Engineering", "2024");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReport, response.getBody());
        assertEquals(15L, response.getBody().total());
        assertEquals(10L, response.getBody().approved());
        verify(statsService, times(1)).getFacultyReassignmentStats("Engineering", "2024");
    }

    @Test
    void getGlobalReassignmentStats_ShouldReturnReportDTO() {
        ReportDTO expectedReport = new ReportDTO(100L, 20L, 70L, 10L, 30L, 40L, 30L);
        when(statsService.getGlobalReassignmentStats()).thenReturn(expectedReport);

        ResponseEntity<ReportDTO> response = statsController.getGlobalReassignmentStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReport, response.getBody());
        assertEquals(100L, response.getBody().total());
        assertEquals(70L, response.getBody().approved());
        verify(statsService, times(1)).getGlobalReassignmentStats();
    }

    @Test
    void getRequestStats_ShouldReturnRequestStats() {
        RequestStats expectedStats = new RequestStats(100L, 20L, 70L, 10L);
        when(statsService.getRequestStats()).thenReturn(expectedStats);

        ResponseEntity<RequestStats> response = statsController.getRequestStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedStats, response.getBody());
        assertEquals(100L, response.getBody().total());
        assertEquals(20L, response.getBody().pending());
        verify(statsService, times(1)).getRequestStats();
    }

    @Test
    void getCourseReassignmentStats_WhenServiceReturnsEmpty_ShouldReturnEmptyReport() {
        ReportDTO emptyReport = new ReportDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        when(statsService.getCourseReassignmentStats("UNKNOWN")).thenReturn(emptyReport);

        ResponseEntity<ReportDTO> response = statsController.getCourseReassignmentStats("UNKNOWN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyReport, response.getBody());
        assertEquals(0L, response.getBody().total());
        assertEquals(0L, response.getBody().approved());
    }

    @Test
    void getAllEndpoints_ShouldCallServiceExactlyOnce() {
        ReportDTO report = new ReportDTO(10L, 2L, 6L, 2L, 3L, 4L, 3L);
        RequestStats stats = new RequestStats(50L, 10L, 30L, 10L);

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

        assertThrows(RuntimeException.class, () -> statsController.getCourseReassignmentStats("MATH101"));
        verify(statsService, times(1)).getCourseReassignmentStats("MATH101");
    }

    @Test
    void getFacultyReassignmentStats_WithDifferentPlan_ShouldUseCorrectPlan() {
        ReportDTO expectedReport = new ReportDTO(25L, 5L, 15L, 5L, 8L, 10L, 7L);
        when(statsService.getFacultyReassignmentStats("Science", "2025B")).thenReturn(expectedReport);

        ResponseEntity<ReportDTO> response = statsController.getFacultyReassignmentStats("Science", "2025B");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedReport, response.getBody());
        assertEquals(25L, response.getBody().total());
        assertEquals(15L, response.getBody().approved());
        verify(statsService, times(1)).getFacultyReassignmentStats("Science", "2025B");
    }
}