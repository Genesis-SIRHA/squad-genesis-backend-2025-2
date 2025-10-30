package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.RequestPeriodDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.services.PeriodService;
import java.time.LocalDate;

import edu.dosw.services.Validators.RequestPeriodValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestPeriodValidatorTest {

    @Mock
    private PeriodService periodService;

    @InjectMocks
    private RequestPeriodValidator requestPeriodValidator;

    private RequestPeriodDTO validRequestPeriodDTO;

    @BeforeEach
    void setUp() {
        validRequestPeriodDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 30),
                "2024",
                "1",
                true
        );
    }

    @Test
    void currentYearValidator_WithValidYear_ShouldNotThrowException() {
        when(periodService.getYear()).thenReturn("2024");

        assertDoesNotThrow(() ->
                requestPeriodValidator.currentYearValidator(validRequestPeriodDTO));
    }

    @Test
    void currentYearValidator_WithInvalidYear_ShouldThrowBusinessException() {
        when(periodService.getYear()).thenReturn("2024");

        RequestPeriodDTO invalidYearDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 30),
                "2023",
                "1",
                true
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> requestPeriodValidator.currentYearValidator(invalidYearDTO)
        );

        assertEquals("This year is invalid because is not the current year: 2023", exception.getMessage());
    }

    @Test
    void currentPeriodValidator_WithValidPeriod_ShouldNotThrowException() {
        when(periodService.getPeriod()).thenReturn("1");

        assertDoesNotThrow(() ->
                requestPeriodValidator.currentPeriodValidator(validRequestPeriodDTO));
    }

    @Test
    void currentPeriodValidator_WithInvalidPeriod_ShouldThrowBusinessException() {
        when(periodService.getPeriod()).thenReturn("1");

        RequestPeriodDTO invalidPeriodDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 30),
                "2024",
                "2",
                true
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> requestPeriodValidator.currentPeriodValidator(invalidPeriodDTO)
        );

        assertEquals("This period is invalid because is not the current period: 2", exception.getMessage());
    }

    @Test
    void initialDateValidator_WithValidInitialDate_ShouldNotThrowException() {
        when(periodService.getYear()).thenReturn("2024");

        assertDoesNotThrow(() ->
                requestPeriodValidator.initialDateValidator(validRequestPeriodDTO));
    }

    @Test
    void initialDateValidator_WithInitialDateAfterFinalDate_ShouldThrowBusinessException() {
        // No necesita stubbing de periodService ya que la validación de fechas no lo usa
        RequestPeriodDTO invalidDateDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 1, 30),
                "2024",
                "1",
                true
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> requestPeriodValidator.initialDateValidator(invalidDateDTO)
        );

        assertEquals("The initial date is after the final date", exception.getMessage());
    }

    @Test
    void initialDateValidator_WithInitialDateInDifferentYear_ShouldThrowBusinessException() {
        when(periodService.getYear()).thenReturn("2024");

        RequestPeriodDTO invalidYearDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2023, 12, 15),
                LocalDate.of(2024, 1, 30),
                "2024",
                "1",
                true
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> requestPeriodValidator.initialDateValidator(invalidYearDTO)
        );

        assertEquals("The initial date is not in the current year", exception.getMessage());
    }

    @Test
    void finalDateValidator_WithValidFinalDate_ShouldNotThrowException() {
        when(periodService.getYear()).thenReturn("2024");

        assertDoesNotThrow(() ->
                requestPeriodValidator.finalDateValidator(validRequestPeriodDTO));
    }

    @Test
    void finalDateValidator_WithFinalDateInDifferentYear_ShouldThrowBusinessException() {
        when(periodService.getYear()).thenReturn("2024");

        RequestPeriodDTO invalidYearDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2024, 12, 15),
                LocalDate.of(2025, 1, 5),
                "2024",
                "1",
                true
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> requestPeriodValidator.finalDateValidator(invalidYearDTO)
        );

        assertEquals("The final date is not in the current year", exception.getMessage());
    }

    @Test
    void createRequestPeriod_WithValidData_ShouldNotThrowException() {
        when(periodService.getYear()).thenReturn("2024");
        when(periodService.getPeriod()).thenReturn("1");

        assertDoesNotThrow(() ->
                requestPeriodValidator.createRequestPeriod(validRequestPeriodDTO));
    }



    @Test
    void updateRequestPeriod_WithValidData_ShouldNotThrowException() {
        when(periodService.getYear()).thenReturn("2024");
        when(periodService.getPeriod()).thenReturn("1");

        assertDoesNotThrow(() ->
                requestPeriodValidator.updateRequestPeriod(validRequestPeriodDTO));
    }

    @Test
    void updateRequestPeriod_WithInvalidPeriod_ShouldThrowBusinessException() {
        when(periodService.getYear()).thenReturn("2024");
        when(periodService.getPeriod()).thenReturn("1");

        RequestPeriodDTO invalidDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 30),
                "2024",
                "2",
                true
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> requestPeriodValidator.updateRequestPeriod(invalidDTO)
        );

        assertEquals("This period is invalid because is not the current period: 2", exception.getMessage());
    }



    @Test
    void finalDateValidator_WithFinalDateInCurrentYear_ShouldNotThrowException() {
        when(periodService.getYear()).thenReturn("2024");

        RequestPeriodDTO validDTO = new RequestPeriodDTO(
                "period-123",
                LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 12, 31),
                "2024",
                "1",
                true
        );

        assertDoesNotThrow(() ->
                requestPeriodValidator.finalDateValidator(validDTO));
    }
}