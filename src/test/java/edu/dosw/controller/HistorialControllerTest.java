package edu.dosw.controller;

import edu.dosw.model.Historial;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.services.HistorialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialControllerTest {

    @Mock
    private HistorialService historialService;

    @InjectMocks
    private HistorialController historialController;

    @Test
    void getAllHistorial_WhenHistorialExists_ShouldReturnHistorialList() {
        Historial historial1 = new Historial.HistorialBuilder()
                .studentId("STU001")
                .groupCode("GROUP001")
                .status(HistorialStatus.ON_GOING)
                .year("2024")
                .period("1")
                .build();
        Historial historial2 = new Historial.HistorialBuilder()
                .studentId("STU002")
                .groupCode("GROUP002")
                .status(HistorialStatus.FINISHED)
                .year("2024")
                .period("1")
                .build();
        List<Historial> expectedHistorial = Arrays.asList(historial1, historial2);

        when(historialService.getAllHistorial()).thenReturn(expectedHistorial);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorial();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("GROUP001", response.getBody().get(0).getGroupCode());
        verify(historialService, times(1)).getAllHistorial();
    }

    @Test
    void getAllHistorial_WhenNoHistorialExists_ShouldReturnEmptyList() {
        List<Historial> emptyList = List.of();

        when(historialService.getAllHistorial()).thenReturn(emptyList);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorial();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(historialService, times(1)).getAllHistorial();
    }

    @Test
    void getAllHistorial_WhenServiceReturnsNull_ShouldReturnEmptyResponse() {
        when(historialService.getAllHistorial()).thenReturn(null);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorial();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(historialService, times(1)).getAllHistorial();
    }

    @Test
    void getAllHistorial_WhenServiceThrowsException_ShouldPropagateException() {
        when(historialService.getAllHistorial()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> historialController.getAllHistorial());
        verify(historialService, times(1)).getAllHistorial();
    }

    @Test
    void getAllHistorial_WithMultipleHistorial_ShouldReturnCorrectOrder() {
        Historial historial1 = new Historial.HistorialBuilder()
                .studentId("STU001")
                .groupCode("GROUP001")
                .status(HistorialStatus.ON_GOING)
                .year("2024")
                .period("1")
                .build();
        Historial historial2 = new Historial.HistorialBuilder()
                .studentId("STU002")
                .groupCode("GROUP002")
                .status(HistorialStatus.FINISHED)
                .year("2024")
                .period("1")
                .build();
        Historial historial3 = new Historial.HistorialBuilder()
                .studentId("STU003")
                .groupCode("GROUP003")
                .status(HistorialStatus.SWAPPED)
                .year("2024")
                .period("1")
                .build();
        List<Historial> expectedHistorial = Arrays.asList(historial1, historial2, historial3);

        when(historialService.getAllHistorial()).thenReturn(expectedHistorial);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorial();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        assertEquals("GROUP001", response.getBody().get(0).getGroupCode());
        assertEquals("GROUP003", response.getBody().get(2).getGroupCode());
        verify(historialService, times(1)).getAllHistorial();
    }

    @Test
    void getAllHistorial_ShouldCallServiceExactlyOnce() {
        Historial historial = new Historial.HistorialBuilder()
                .studentId("STU001")
                .groupCode("GROUP001")
                .status(HistorialStatus.ON_GOING)
                .year("2024")
                .period("1")
                .build();
        List<Historial> historialList = List.of(historial);

        when(historialService.getAllHistorial()).thenReturn(historialList);

        historialController.getAllHistorial();

        verify(historialService, times(1)).getAllHistorial();
        verifyNoMoreInteractions(historialService);
    }

    @Test
    void getAllHistorial_WithDifferentStatuses_ShouldReturnAllStatuses() {
        Historial onGoing = new Historial.HistorialBuilder()
                .studentId("STU001")
                .groupCode("GROUP001")
                .status(HistorialStatus.ON_GOING)
                .year("2024")
                .period("1")
                .build();
        Historial finished = new Historial.HistorialBuilder()
                .studentId("STU002")
                .groupCode("GROUP002")
                .status(HistorialStatus.FINISHED)
                .year("2024")
                .period("1")
                .build();
        Historial cancelled = new Historial.HistorialBuilder()
                .studentId("STU003")
                .groupCode("GROUP003")
                .status(HistorialStatus.CANCELLED)
                .year("2024")
                .period("1")
                .build();
        Historial failed = new Historial.HistorialBuilder()
                .studentId("STU004")
                .groupCode("GROUP004")
                .status(HistorialStatus.FAILED)
                .year("2024")
                .period("1")
                .build();
        List<Historial> expectedHistorial = Arrays.asList(onGoing, finished, cancelled, failed);

        when(historialService.getAllHistorial()).thenReturn(expectedHistorial);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorial();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, response.getBody().size());
        assertEquals(HistorialStatus.ON_GOING, response.getBody().get(0).getStatus());
        assertEquals(HistorialStatus.FINISHED, response.getBody().get(1).getStatus());
        assertEquals(HistorialStatus.CANCELLED, response.getBody().get(2).getStatus());
        assertEquals(HistorialStatus.FAILED, response.getBody().get(3).getStatus());
    }

    @Test
    void getAllHistorial_WithLargeDataset_ShouldHandlePerformance() {
        List<Historial> largeList = Arrays.asList(
                new Historial.HistorialBuilder()
                        .studentId("STU001")
                        .groupCode("GROUP001")
                        .status(HistorialStatus.ON_GOING)
                        .year("2024")
                        .period("1")
                        .build(),
                new Historial.HistorialBuilder()
                        .studentId("STU002")
                        .groupCode("GROUP002")
                        .status(HistorialStatus.ON_GOING)
                        .year("2024")
                        .period("1")
                        .build(),
                new Historial.HistorialBuilder()
                        .studentId("STU003")
                        .groupCode("GROUP003")
                        .status(HistorialStatus.ON_GOING)
                        .year("2024")
                        .period("1")
                        .build(),
                new Historial.HistorialBuilder()
                        .studentId("STU004")
                        .groupCode("GROUP004")
                        .status(HistorialStatus.ON_GOING)
                        .year("2024")
                        .period("1")
                        .build(),
                new Historial.HistorialBuilder()
                        .studentId("STU005")
                        .groupCode("GROUP005")
                        .status(HistorialStatus.ON_GOING)
                        .year("2024")
                        .period("1")
                        .build()
        );

        when(historialService.getAllHistorial()).thenReturn(largeList);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorial();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().size());
        verify(historialService, times(1)).getAllHistorial();
    }

    @Test
    void getAllHistorial_ShouldReturnCorrectResponseType() {
        Historial historial = new Historial.HistorialBuilder()
                .studentId("STU001")
                .groupCode("GROUP001")
                .status(HistorialStatus.ON_GOING)
                .year("2024")
                .period("1")
                .build();
        List<Historial> historialList = List.of(historial);

        when(historialService.getAllHistorial()).thenReturn(historialList);

        ResponseEntity<List<Historial>> response = historialController.getAllHistorial();

        assertInstanceOf(ResponseEntity.class, response);
        assertInstanceOf(List.class, response.getBody());
        assertEquals(Historial.class, response.getBody().get(0).getClass());
    }

}