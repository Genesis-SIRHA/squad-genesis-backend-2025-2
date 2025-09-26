package edu.dosw.controller;

import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PensumControllerTest {

    @Mock
    private PemsumService pemsumService;

    @InjectMocks
    private PemsumController pemsumController;

    private Pemsum mockPemsum;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockPemsum = new Pemsum.Builder()
                .studentId("student123")
                .studentName("John Doe")
                .facultyName("Engineering")
                .facultyPlan("PlanA")
                .totalCredits(100)
                .approvedCredits(60)
                .build();
    }

    @Test
    void shouldReturnPemsumSuccessfully() {
        // Arrange
        String studentId = "student123";
        when(pemsumService.getPemsum(studentId)).thenReturn(mockPemsum);

        // Act
        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPemsum, response.getBody());
        verify(pemsumService).getPemsum(studentId);
    }
}
