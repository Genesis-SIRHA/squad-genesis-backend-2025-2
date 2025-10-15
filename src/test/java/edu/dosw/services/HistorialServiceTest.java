package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import edu.dosw.model.Historial;
import edu.dosw.repositories.HistorialRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class HistorialServiceTest {

  @Mock private HistorialRepository historialRepository;

  @InjectMocks private HistorialService historialService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getCurrentSessionsByStudentIdAndPeriod_shouldReturnGroupCodes() {
    // Arrange
    Historial h1 = new Historial();
    h1.setGroupCode("G1");
    Historial h2 = new Historial();
    h2.setGroupCode("G2");

    ArrayList<Historial> mockHistorial = new ArrayList<>();
    mockHistorial.add(h1);
    mockHistorial.add(h2);

    when(historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod("S1", "2025", "1"))
        .thenReturn(mockHistorial);

    // Act
    List<String> result =
        historialService.getCurrentSessionsByStudentIdAndPeriod("S1", "2025", "1");

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.contains("G1"));
    assertTrue(result.contains("G2"));
  }

  @Test
  void getCurrentSessionsByStudentIdAndPeriod_shouldReturnEmptyListIfNoHistorial() {
    // Arrange
    when(historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod("S1", "2025", "1"))
        .thenReturn(new ArrayList<>());

    // Act
    List<String> result =
        historialService.getCurrentSessionsByStudentIdAndPeriod("S1", "2025", "1");

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
