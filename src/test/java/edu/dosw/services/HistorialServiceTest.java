package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Historial;
import edu.dosw.services.Validators.HistorialValidator;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.repositories.HistorialRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    @Mock private HistorialValidator historialValidator;

    @Mock private HistorialRepository historialRepository;

    @Mock private PeriodService periodService;

    @InjectMocks private HistorialService historialService;

  private Historial historial1;
  private Historial historial2;
  private List<Historial> historials;

  @BeforeEach
  void setUp() {
    historial1 = new Historial();
    historial1.setStudentId("123");
    historial1.setYear("2024");
    historial1.setPeriod("1");
    historial1.setGroupCode("MAT101");
    historial1.setStatus(HistorialStatus.ON_GOING);

    historial2 = new Historial();
    historial2.setStudentId("123");
    historial2.setYear("2023");
    historial2.setPeriod("2");
    historial2.setGroupCode("FIS201");
    historial2.setStatus(HistorialStatus.FINISHED);

    historials = Arrays.asList(historial1, historial2);
  }

  @Test
  void getAllHistorialsByStudentId_ShouldReturnHistorials() {
    String studentId = "123";
    when(historialRepository.findByStudentId(studentId)).thenReturn(new ArrayList<>(historials));

    List<Historial> result = historialService.getAllHistorialsByStudentId(studentId);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(historialRepository, times(1)).findByStudentId(studentId);
  }

    @Test
    void getCurrentSessionsByStudentIdAndPeriod_ShouldReturnGroupCodes() {
        String studentId = "STUD123";
        String year = "2024";
        String period = "1";

        List<Historial> historials =
                Arrays.asList(
                        createHistorial("GROUP1", studentId, year, period),
                        createHistorial("GROUP2", studentId, year, period));

        when(historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(
                studentId, year, period))
                .thenReturn(new java.util.ArrayList<>(historials));

        List<String> result =
                historialService.getCurrentSessionsByStudentIdAndPeriod(studentId, year, period);

        assertEquals(2, result.size());
        assertTrue(result.contains("GROUP1"));
        assertTrue(result.contains("GROUP2"));
        verify(historialRepository)
                .findCurrentSessionsByStudentIdAndYearAndPeriod(studentId, year, period);
    }

  @Test
  void getAllHistorialsByStudentId_WhenNoHistorials_ShouldThrowBusinessException() {
    String studentId = "999";
    when(historialRepository.findByStudentId(studentId)).thenReturn(new ArrayList<>());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> historialService.getAllHistorialsByStudentId(studentId));

    assertEquals(
        "Error al obtener los historiales del estudiante " + studentId, exception.getMessage());
    verify(historialRepository, times(1)).findByStudentId(studentId);
  }

  @Test
  void getGroupCodesByStudentIdAndPeriod_ShouldReturnGroupCodes() {
    String studentId = "123";
    String year = "2024";
    String period = "1";
    when(historialRepository.findHistorialByStudentIdAndYearAndPeriod(studentId, year, period))
        .thenReturn(new ArrayList<>(Arrays.asList(historial1)));

    List<String> result =
        historialService.getGroupCodesByStudentIdAndPeriod(studentId, year, period);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("MAT101", result.get(0));
    verify(historialRepository, times(1))
        .findHistorialByStudentIdAndYearAndPeriod(studentId, year, period);
  }
    @Test
    void getCurrentSessionsByStudentIdAndPeriod_WithEmptyList_ShouldReturnEmptyList() {
        String studentId = "STUD123";
        String year = "2024";
        String period = "1";

        when(historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(
                studentId, year, period))
                .thenReturn(new java.util.ArrayList<>());

        List<String> result =
                historialService.getCurrentSessionsByStudentIdAndPeriod(studentId, year, period);

        assertTrue(result.isEmpty());
        verify(historialRepository)
                .findCurrentSessionsByStudentIdAndYearAndPeriod(studentId, year, period);
    }

    @Test
    void getByStudentIdAndGroupCode_WithExistingHistorial_ShouldReturnHistorial() {
        String studentId = "STUD123";
        String groupCode = "GROUP1";
        Historial expectedHistorial = createHistorial(groupCode, studentId, "2024", "1");

        when(historialRepository.findByStudentIdAndGroupCode(studentId, groupCode))
                .thenReturn(expectedHistorial);

        Historial result = historialService.getByStudentIdAndGroupCode(studentId, groupCode);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals(groupCode, result.getGroupCode());
        verify(historialRepository).findByStudentIdAndGroupCode(studentId, groupCode);
    }

    @Test
    void getByStudentIdAndGroupCode_WithNonExistingHistorial_ShouldThrowResourceNotFoundException() {
        String studentId = "STUD123";
        String groupCode = "GROUP1";

        when(historialRepository.findByStudentIdAndGroupCode(studentId, groupCode)).thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> historialService.getByStudentIdAndGroupCode(studentId, groupCode));

        assertTrue(exception.getMessage().contains("historial not found"));
        verify(historialRepository).findByStudentIdAndGroupCode(studentId, groupCode);
    }

    @Test
    void getSessionsByStudentIdYearAndPeriod_ShouldReturnHistorials() {
        String studentId = "STUD123";
        String year = "2024";
        String period = "1";

        List<Historial> expectedHistorials =
                Arrays.asList(
                        createHistorial("GROUP1", studentId, year, period),
                        createHistorial("GROUP2", studentId, year, period));

        when(historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(
                studentId, year, period))
                .thenReturn(new java.util.ArrayList<>(expectedHistorials));

        List<Historial> result =
                historialService.getSessionsByStudentIdYearAndPeriod(studentId, year, period);

        assertEquals(2, result.size());
        verify(historialRepository)
                .findCurrentSessionsByStudentIdAndYearAndPeriod(studentId, year, period);
    }

    @Test
    void addHistorial_WithValidData_ShouldReturnSavedHistorial() {
        HistorialDTO historialDTO = new HistorialDTO("STUD123", "GROUP1", HistorialStatus.ON_GOING);
        Historial existingHistorial = createHistorial("GROUP1", "STUD123", "2023", "1");
        Historial newHistorial = createHistorial("GROUP1", "STUD123", "2024", "1");

        when(historialRepository.findByStudentIdAndGroupCode("STUD123", "GROUP1"))
                .thenReturn(existingHistorial);
        doNothing().when(historialValidator).validateHistorialCreation(historialDTO);
        when(periodService.getYear()).thenReturn("2024");
        when(periodService.getPeriod()).thenReturn("1");
        when(historialRepository.save(any(Historial.class))).thenReturn(newHistorial);

        Historial result = historialService.addHistorial(historialDTO);

        assertNotNull(result);
        verify(historialRepository).findByStudentIdAndGroupCode("STUD123", "GROUP1");
        verify(historialValidator).validateHistorialCreation(historialDTO);
        verify(periodService).getYear();
        verify(periodService).getPeriod();
        verify(historialRepository).save(any(Historial.class));
    }

    @Test
    void addHistorial_WithNonExistingHistorial_ShouldThrowResourceNotFoundException() {
        HistorialDTO historialDTO = new HistorialDTO("STUD123", "GROUP1", HistorialStatus.ON_GOING);

        when(historialRepository.findByStudentIdAndGroupCode("STUD123", "GROUP1")).thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class, () -> historialService.addHistorial(historialDTO));

        assertTrue(exception.getMessage().contains("historial not found"));
        verify(historialRepository).findByStudentIdAndGroupCode("STUD123", "GROUP1");
        verify(historialValidator, never()).validateHistorialCreation(any());
        verify(historialRepository, never()).save(any());
    }

    @Test
    void updateHistorial_WithValidData_ShouldReturnUpdatedHistorial() {
        String studentId = "STUD123";
        String groupCode = "GROUP1";
        HistorialStatus newStatus = HistorialStatus.CANCELLED;
        Historial existingHistorial = createHistorial(groupCode, studentId, "2024", "1");

        when(historialRepository.findByStudentIdAndGroupCode(studentId, groupCode))
                .thenReturn(existingHistorial);
        doNothing()
                .when(historialValidator)
                .historialUpdateValidator(existingHistorial.getStatus(), newStatus);
        when(historialRepository.save(existingHistorial)).thenReturn(existingHistorial);

        Historial result = historialService.updateHistorial(studentId, groupCode, newStatus);

        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(historialRepository).findByStudentIdAndGroupCode(studentId, groupCode);
        verify(historialValidator).historialUpdateValidator(HistorialStatus.ON_GOING, newStatus);
        verify(historialRepository).save(existingHistorial);
    }

    @Test
    void updateHistorial_WithNonExistingHistorial_ShouldThrowResourceNotFoundException() {
        String studentId = "STUD123";
        String groupCode = "GROUP1";
        HistorialStatus newStatus = HistorialStatus.CANCELLED;

        when(historialRepository.findByStudentIdAndGroupCode(studentId, groupCode)).thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> historialService.updateHistorial(studentId, groupCode, newStatus));

        assertTrue(exception.getMessage().contains("historial not found"));
        verify(historialRepository).findByStudentIdAndGroupCode(studentId, groupCode);
        verify(historialValidator, never()).historialUpdateValidator(any(), any());
        verify(historialRepository, never()).save(any());
    }

    @Test
    void getAllHistorial_ShouldReturnAllHistorials() {
        List<Historial> expectedHistorials =
                Arrays.asList(
                        createHistorial("GROUP1", "STUD123", "2024", "1"),
                        createHistorial("GROUP2", "STUD456", "2024", "1"));

        when(historialRepository.findAll()).thenReturn(expectedHistorials);

        List<Historial> result = historialService.getAllHistorial();

        assertEquals(2, result.size());
        verify(historialRepository).findAll();
    }

    private Historial createHistorial(
            String groupCode, String studentId, String year, String period) {
        return new Historial.HistorialBuilder()
                .studentId(studentId)
                .groupCode(groupCode)
                .status(HistorialStatus.ON_GOING)
                .year(year)
                .period(period)
                .build();
    }
}
