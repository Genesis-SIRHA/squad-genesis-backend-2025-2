package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PensumControllerTest {

  @Mock private PemsumService pemsumService;

  @InjectMocks private PemsumController pemsumController;

  private Pemsum mockPemsum;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    mockPemsum =
        new Pemsum.Builder()
            .studentId("student123")
            .studentName("John Doe")
            .facultyName("Engineering")
            .facultyPlan("PlanA")
            .totalCredits(100)
            .approvedCredits(60)
            .build();
  }

  @Test
  void shouldReturnPensumSuccessfully() {
    // Arrange
    String studentId = "student123";
    when(pemsumService.getPemsum(studentId)).thenReturn(mockPemsum);

    // Act
    Pemsum result = pemsumController.getPemsum(studentId);

    // Assert
    assertNotNull(result);
    assertEquals(mockPemsum, result);
    verify(pemsumService).getPemsum(studentId);
  }
}
