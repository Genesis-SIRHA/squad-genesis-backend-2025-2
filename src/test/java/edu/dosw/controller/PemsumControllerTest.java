package edu.dosw.controller;

import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PemsumControllerTest {

    @Mock
    private PemsumService pemsumService;

    @InjectMocks
    private PemsumController pemsumController;

    private final String STUDENT_ID = "12345";

    @Test
    void getPemsum_ShouldReturnPemsum() {
        Pemsum mockPemsum = new Pemsum.Builder()
                .studentId(STUDENT_ID)
                .studentName("John Doe")
                .facultyName("Engineering")
                .facultyPlan("2024")
                .approvedCredits(60)
                .totalCredits(120)
                .courses(List.of())
                .build();

        when(pemsumService.getPemsum(STUDENT_ID)).thenReturn(mockPemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPemsum, response.getBody());
        verify(pemsumService).getPemsum(STUDENT_ID);
    }

    @Test
    void getPemsum_ShouldCallServiceWithCorrectParameter() {
        Pemsum mockPemsum = new Pemsum.Builder().build();
        when(pemsumService.getPemsum(STUDENT_ID)).thenReturn(mockPemsum);

        pemsumController.getPemsum(STUDENT_ID);

        verify(pemsumService).getPemsum(STUDENT_ID);
    }

    @Test
    void getCompletedCoursesPercentage_ShouldReturnPercentage() {
        Double expectedPercentage = 75.5;
        when(pemsumService.getCompletedCoursesPercentage(STUDENT_ID)).thenReturn(expectedPercentage);

        ResponseEntity<Double> response = pemsumController.getCompletedCoursesPercentage(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPercentage, response.getBody());
    }

    @Test
    void getCompletedCoursesPercentage_ShouldHandleZeroPercentage() {
        when(pemsumService.getCompletedCoursesPercentage(STUDENT_ID)).thenReturn(0.0);

        ResponseEntity<Double> response = pemsumController.getCompletedCoursesPercentage(STUDENT_ID);

        assertEquals(0.0, response.getBody());
    }

    @Test
    void getCompletedCoursesPercentage_ShouldHandleFullPercentage() {
        when(pemsumService.getCompletedCoursesPercentage(STUDENT_ID)).thenReturn(100.0);

        ResponseEntity<Double> response = pemsumController.getCompletedCoursesPercentage(STUDENT_ID);

        assertEquals(100.0, response.getBody());
    }

    @Test
    void getStudentCoursesStatus_ShouldReturnCoursesStatus() {
        Map<String, String> mockStatus = Map.of("MATH101", "APPROVED", "PHYS101", "PENDING");
        when(pemsumService.getStudentCoursesStatus(STUDENT_ID)).thenReturn(mockStatus);

        ResponseEntity<Map<String, String>> response = pemsumController.getStudentCoursesStatus(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStatus, response.getBody());
    }

    @Test
    void getStudentCoursesStatus_ShouldReturnEmptyMap() {
        Map<String, String> emptyMap = Map.of();
        when(pemsumService.getStudentCoursesStatus(STUDENT_ID)).thenReturn(emptyMap);

        ResponseEntity<Map<String, String>> response = pemsumController.getStudentCoursesStatus(STUDENT_ID);

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getStudentCoursesStatus_ShouldHandleMultipleCourses() {
        Map<String, String> multipleCourses = Map.of(
                "MATH101", "APPROVED",
                "PHYS101", "PENDING",
                "CHEM101", "FAILED",
                "BIO101", "IN_PROGRESS"
        );
        when(pemsumService.getStudentCoursesStatus(STUDENT_ID)).thenReturn(multipleCourses);

        ResponseEntity<Map<String, String>> response = pemsumController.getStudentCoursesStatus(STUDENT_ID);

        assertEquals(4, response.getBody().size());
    }

    @Test
    void controller_ShouldBeInitialized() {
        assertNotNull(pemsumController);
    }

    @Test
    void service_ShouldBeInjected() {
        assertNotNull(pemsumService);
    }
}