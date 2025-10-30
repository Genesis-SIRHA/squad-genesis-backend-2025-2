package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Historial;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.repositories.HistorialRepository;
import edu.dosw.services.Validators.HistorialValidator;
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

    when(historialRepository.findHistorialByStudentIdAndYearAndPeriod(studentId, year, period))
        .thenReturn(new java.util.ArrayList<>(historials));

    List<String> result =
        historialService.getGroupCodesByStudentIdAndPeriod(studentId, year, period);

    assertEquals(2, result.size());
    assertTrue(result.contains("GROUP1"));
    assertTrue(result.contains("GROUP2"));
    verify(historialRepository).findHistorialByStudentIdAndYearAndPeriod(studentId, year, period);
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

    when(historialRepository.findHistorialByStudentIdAndYearAndPeriod(studentId, year, period))
        .thenReturn(new java.util.ArrayList<>());

    List<String> result =
        historialService.getGroupCodesByStudentIdAndPeriod(studentId, year, period);

    assertTrue(result.isEmpty());
    verify(historialRepository).findHistorialByStudentIdAndYearAndPeriod(studentId, year, period);
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
    @Test
    void addHistorial_WithValidData_ShouldCreateNewHistorial() {
        HistorialDTO dto = new HistorialDTO("student123", "GROUP101", HistorialStatus.ON_GOING);

        when(periodService.getYear()).thenReturn("2024");
        when(periodService.getPeriod()).thenReturn("1");
        when(historialRepository.findByStudentIdAndGroupCode("student123", "GROUP101")).thenReturn(null);
        when(historialRepository.save(any(Historial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Historial result = historialService.addHistorial(dto);

        assertNotNull(result);
        assertEquals("student123", result.getStudentId());
        assertEquals("GROUP101", result.getGroupCode());
        assertEquals(HistorialStatus.ON_GOING, result.getStatus());
        assertEquals("2024", result.getYear());
        assertEquals("1", result.getPeriod());

        verify(historialValidator).validateHistorialCreation(dto);
        verify(historialRepository).findByStudentIdAndGroupCode("student123", "GROUP101");
        verify(historialRepository).save(any(Historial.class));
    }

    @Test
    void addHistorial_WithExistingHistorial_ShouldThrowBusinessException() {
        HistorialDTO dto = new HistorialDTO("student123", "GROUP101", HistorialStatus.ON_GOING);
        Historial existingHistorial = createHistorial("GROUP101", "student123", "2024", "1");

        when(historialRepository.findByStudentIdAndGroupCode("student123", "GROUP101")).thenReturn(existingHistorial);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> historialService.addHistorial(dto)
        );

        assertEquals("Ya existe un historial para el estudiante student123 y el grupo GROUP101", exception.getMessage());

        verify(historialValidator).validateHistorialCreation(dto);
        verify(historialRepository).findByStudentIdAndGroupCode("student123", "GROUP101");
        verify(historialRepository, never()).save(any(Historial.class));
    }

    @Test
    void addHistorial_WhenSaveFails_ShouldThrowBusinessException() {
        HistorialDTO dto = new HistorialDTO("student123", "GROUP101", HistorialStatus.ON_GOING);

        when(periodService.getYear()).thenReturn("2024");
        when(periodService.getPeriod()).thenReturn("1");
        when(historialRepository.findByStudentIdAndGroupCode("student123", "GROUP101")).thenReturn(null);
        when(historialRepository.save(any(Historial.class))).thenThrow(new RuntimeException("Database error"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> historialService.addHistorial(dto)
        );

        assertEquals("Error al guardar el historial", exception.getMessage());
        assertNotNull(exception.getCause());

        verify(historialValidator).validateHistorialCreation(dto);
        verify(historialRepository).findByStudentIdAndGroupCode("student123", "GROUP101");
        verify(historialRepository).save(any(Historial.class));
    }

    @Test
    void getSessionsByCourses_WithMatchingCourses_ShouldReturnLastHistorial() {
        String studentId = "student123";

        Course course1 = new Course();
        course1.setAbbreviation("MAT101");

        Course course2 = new Course();
        course2.setAbbreviation("FIS201");

        List<Course> courses = Arrays.asList(course1, course2);

        Historial historial1v1 = createHistorial("MAT101", studentId, "2023", "1");
        Historial historial1v2 = createHistorial("MAT101", studentId, "2023", "2");
        Historial historial2 = createHistorial("FIS201", studentId, "2024", "1");
        Historial historial3 = createHistorial("QUI301", studentId, "2024", "1");

        ArrayList<Historial> completeHistorial = new ArrayList<>(Arrays.asList(historial1v1, historial1v2, historial2, historial3));

        when(historialRepository.findByStudentId(studentId)).thenReturn(completeHistorial);

        List<Historial> result = historialService.getSessionsByCourses(studentId, courses);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(h -> h.getGroupCode().equals("MAT101") && h.getPeriod().equals("2")));
        assertTrue(result.stream().anyMatch(h -> h.getGroupCode().equals("FIS201")));
        assertFalse(result.stream().anyMatch(h -> h.getGroupCode().equals("QUI301")));

        verify(historialRepository).findByStudentId(studentId);
    }

    @Test
    void getSessionsByCourses_WithNoMatchingCourses_ShouldReturnEmptyList() {
        String studentId = "student123";

        Course course1 = new Course();
        course1.setAbbreviation("BIO401");

        List<Course> courses = Arrays.asList(course1);

        Historial historial1 = createHistorial("MAT101", studentId, "2024", "1");
        Historial historial2 = createHistorial("FIS201", studentId, "2024", "1");

        ArrayList<Historial> completeHistorial = new ArrayList<>(Arrays.asList(historial1, historial2));

        when(historialRepository.findByStudentId(studentId)).thenReturn(completeHistorial);

        List<Historial> result = historialService.getSessionsByCourses(studentId, courses);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(historialRepository).findByStudentId(studentId);
    }

    @Test
    void getSessionsByCourses_WithEmptyCourses_ShouldReturnEmptyList() {
        String studentId = "student123";
        List<Course> courses = new ArrayList<>();

        Historial historial1 = createHistorial("MAT101", studentId, "2024", "1");

        ArrayList<Historial> completeHistorial = new ArrayList<>(Arrays.asList(historial1));

        when(historialRepository.findByStudentId(studentId)).thenReturn(completeHistorial);

        List<Historial> result = historialService.getSessionsByCourses(studentId, courses);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(historialRepository).findByStudentId(studentId);
    }

    @Test
    void getSessionsByCourses_WithNoHistorial_ShouldReturnEmptyList() {
        String studentId = "student123";

        Course course1 = new Course();
        course1.setAbbreviation("MAT101");

        List<Course> courses = Arrays.asList(course1);

        when(historialRepository.findByStudentId(studentId)).thenReturn(new ArrayList<>());

        List<Historial> result = historialService.getSessionsByCourses(studentId, courses);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(historialRepository).findByStudentId(studentId);
    }

    @Test
    void getHistorialByStudentId_WithExistingHistorials_ShouldReturnHistorials() {
        String studentId = "student123";
        ArrayList<Historial> expectedHistorials = new ArrayList<>(Arrays.asList(
                createHistorial("MAT101", studentId, "2024", "1"),
                createHistorial("FIS201", studentId, "2024", "1")
        ));

        when(historialRepository.findByStudentId(studentId)).thenReturn(expectedHistorials);

        List<Historial> result = historialService.getHistorialByStudentId(studentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedHistorials, result);
        verify(historialRepository).findByStudentId(studentId);
    }

    @Test
    void getHistorialByStudentId_WithNoHistorials_ShouldThrowBusinessException() {
        String studentId = "student123";

        when(historialRepository.findByStudentId(studentId)).thenReturn(new ArrayList<>());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> historialService.getHistorialByStudentId(studentId)
        );

        assertEquals("Error al obtener los historiales del estudiante " + studentId, exception.getMessage());
        verify(historialRepository).findByStudentId(studentId);
    }

    @Test
    void getHistorialByStudentId_WhenRepositoryReturnsNull_ShouldThrowBusinessException() {
        String studentId = "student123";

        when(historialRepository.findByStudentId(studentId)).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> historialService.getHistorialByStudentId(studentId)
        );

        assertEquals("Error al obtener los historiales del estudiante " + studentId, exception.getMessage());
        verify(historialRepository).findByStudentId(studentId);
    }

    @Test
    void getHistorialByStudentIdAndStatus_WithMatchingHistorials_ShouldReturnFilteredHistorials() {
        String studentId = "student123";
        HistorialStatus status = HistorialStatus.ON_GOING;

        Historial historial1 = createHistorial("MAT101", studentId, "2024", "1");
        historial1.setStatus(HistorialStatus.ON_GOING);

        ArrayList<Historial> expectedHistorials = new ArrayList<>(Arrays.asList(historial1));

        when(historialRepository.findByStudentIdAndStatus(studentId, status)).thenReturn(expectedHistorials);

        List<Historial> result = historialService.getHistorialByStudentIdAndStatus(studentId, status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(HistorialStatus.ON_GOING, result.get(0).getStatus());
        verify(historialRepository).findByStudentIdAndStatus(studentId, status);
    }

    @Test
    void getHistorialByStudentIdAndStatus_WithNoMatchingHistorials_ShouldReturnEmptyList() {
        String studentId = "student123";
        HistorialStatus status = HistorialStatus.CANCELLED;

        when(historialRepository.findByStudentIdAndStatus(studentId, status)).thenReturn(new ArrayList<>());

        List<Historial> result = historialService.getHistorialByStudentIdAndStatus(studentId, status);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(historialRepository).findByStudentIdAndStatus(studentId, status);
    }

    @Test
    void updateHistorial_WhenValidatorThrowsException_ShouldThrowBusinessException() {
        String studentId = "student123";
        String groupCode = "GROUP101";
        HistorialStatus newStatus = HistorialStatus.CANCELLED;

        Historial existingHistorial = createHistorial(groupCode, studentId, "2024", "1");
        existingHistorial.setStatus(HistorialStatus.ON_GOING);

        when(historialRepository.findByStudentIdAndGroupCode(studentId, groupCode)).thenReturn(existingHistorial);
        doThrow(new BusinessException("Transición no permitida"))
                .when(historialValidator).historialUpdateValidator(existingHistorial.getStatus(), newStatus);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> historialService.updateHistorial(studentId, groupCode, newStatus)
        );

        assertEquals("Error al actualizar el historial del estudiante student123 en el grupo GROUP101", exception.getMessage());
        assertNotNull(exception.getCause());

        verify(historialRepository).findByStudentIdAndGroupCode(studentId, groupCode);
        verify(historialValidator).historialUpdateValidator(HistorialStatus.ON_GOING, newStatus);
        verify(historialRepository, never()).save(any(Historial.class));
    }
}
