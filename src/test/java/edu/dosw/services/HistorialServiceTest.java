package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.Historial;
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

    @Mock private HistorialRepository historialRepository;

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
}