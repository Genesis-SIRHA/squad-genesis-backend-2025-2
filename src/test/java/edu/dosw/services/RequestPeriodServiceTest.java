package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreateRequestPeriodDTO;
import edu.dosw.dto.RequestPeriodDTO;
import edu.dosw.dto.UpdateRequestPeriodDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.repositories.RequestPeriodRepository;
import edu.dosw.services.Validators.RequestPeriodValidator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestPeriodServiceTest {

    @Mock
    private RequestPeriodRepository requestPeriodRepository;

    @Mock
    private PeriodService periodService;

    @Mock
    private RequestPeriodValidator requestPeriodValidator;

    @InjectMocks
    private RequestPeriodService requestPeriodService;

    @Test
    void getActivePeriod_WithActivePeriod_ShouldReturnPeriod() {
        RequestPeriodDTO expectedPeriod = new RequestPeriodDTO(
                "123", LocalDate.now().minusDays(10), LocalDate.now().plusDays(10),
                "2024", "1", true
        );

        when(requestPeriodRepository.activePeriod()).thenReturn(expectedPeriod);

        RequestPeriodDTO result = requestPeriodService.getActivePeriod();

        assertNotNull(result);
        assertEquals(expectedPeriod, result);
        verify(requestPeriodRepository).activePeriod();
    }

    @Test
    void getActivePeriod_WithoutActivePeriod_ShouldThrowResourceNotFoundException() {
        when(requestPeriodRepository.activePeriod()).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> requestPeriodService.getActivePeriod()
        );

        assertEquals("There is no active period of creation and answer of requests", exception.getMessage());
        verify(requestPeriodRepository).activePeriod();
    }

    @Test
    void getPeriodById_WithExistingId_ShouldReturnPeriod() {
        String periodId = "123";
        RequestPeriodDTO expectedPeriod = new RequestPeriodDTO(
                periodId, LocalDate.now().minusDays(10), LocalDate.now().plusDays(10),
                "2024", "1", true
        );

        when(requestPeriodRepository.getById(periodId)).thenReturn(expectedPeriod);

        RequestPeriodDTO result = requestPeriodService.getPeriodById(periodId);

        assertNotNull(result);
        assertEquals(expectedPeriod, result);
        verify(requestPeriodRepository).getById(periodId);
    }

    @Test
    void getPeriodById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        String periodId = "non-existing";

        when(requestPeriodRepository.getById(periodId)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> requestPeriodService.getPeriodById(periodId)
        );

        assertEquals("There is no Request period identified with the id: " + periodId, exception.getMessage());
        verify(requestPeriodRepository).getById(periodId);
    }

    @Test
    void getAllPeriods_ShouldReturnAllPeriods() {
        List<RequestPeriodDTO> expectedPeriods = Arrays.asList(
                new RequestPeriodDTO("1", LocalDate.now().minusDays(20), LocalDate.now().minusDays(10), "2024", "1", false),
                new RequestPeriodDTO("2", LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), "2024", "2", true)
        );

        when(requestPeriodRepository.findAll()).thenReturn(expectedPeriods);

        List<RequestPeriodDTO> result = requestPeriodService.getAllPeriods();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedPeriods, result);
        verify(requestPeriodRepository).findAll();
    }

    @Test
    void createActivePeriod_WhenNoActivePeriodExists_ShouldCreateNewPeriod() {
        CreateRequestPeriodDTO createDTO = new CreateRequestPeriodDTO(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(10), "2024", "1"
        );

        when(requestPeriodRepository.activePeriod()).thenReturn(null);
        when(requestPeriodRepository.save(any(RequestPeriodDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RequestPeriodDTO result = requestPeriodService.createActivePeriod(createDTO);

        assertNotNull(result);
        assertTrue(result.isActive());
        assertEquals(createDTO.initialDate(), result.initialDate());
        assertEquals(createDTO.finalDate(), result.finalDate());
        assertEquals(createDTO.year(), result.year());
        assertEquals(createDTO.period(), result.period());

        verify(requestPeriodValidator).createRequestPeriod(any(RequestPeriodDTO.class));
        verify(requestPeriodRepository).save(any(RequestPeriodDTO.class));
    }

    @Test
    void createActivePeriod_WhenActivePeriodExists_ShouldThrowBusinessException() {
        CreateRequestPeriodDTO createDTO = new CreateRequestPeriodDTO(
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(10), "2024", "1"
        );

        RequestPeriodDTO existingPeriod = new RequestPeriodDTO(
                "123", LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), "2024", "1", true
        );

        when(requestPeriodRepository.activePeriod()).thenReturn(existingPeriod);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> requestPeriodService.createActivePeriod(createDTO)
        );

        assertEquals("There is still an active period of creation and answer of requests", exception.getMessage());
        verify(requestPeriodRepository, never()).save(any(RequestPeriodDTO.class));
    }

    @Test
    void updateActivePeriod_WithValidUpdates_ShouldUpdatePeriod() {
        RequestPeriodDTO existingPeriod = new RequestPeriodDTO(
                "123", LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), "2024", "1", true
        );

        UpdateRequestPeriodDTO updateDTO = new UpdateRequestPeriodDTO(
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(15), true
        );

        when(requestPeriodRepository.activePeriod()).thenReturn(existingPeriod);
        when(requestPeriodRepository.save(any(RequestPeriodDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RequestPeriodDTO result = requestPeriodService.updateActivePeriod(updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.initialDate(), result.initialDate());
        assertEquals(updateDTO.finalDate(), result.finalDate());
        assertTrue(result.isActive());
        assertEquals("2024", result.year());
        assertEquals("1", result.period());

        verify(requestPeriodValidator).updateRequestPeriod(any(RequestPeriodDTO.class));
        verify(requestPeriodRepository).save(any(RequestPeriodDTO.class));
    }

    @Test
    void updateActivePeriod_WithNullUpdates_ShouldKeepOriginalValues() {
        LocalDate originalInitialDate = LocalDate.now().minusDays(10);
        LocalDate originalFinalDate = LocalDate.now().plusDays(10);

        RequestPeriodDTO existingPeriod = new RequestPeriodDTO(
                "123", originalInitialDate, originalFinalDate, "2024", "1", true
        );

        UpdateRequestPeriodDTO updateDTO = new UpdateRequestPeriodDTO(null, null, true);

        when(requestPeriodRepository.activePeriod()).thenReturn(existingPeriod);
        when(requestPeriodRepository.save(any(RequestPeriodDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RequestPeriodDTO result = requestPeriodService.updateActivePeriod(updateDTO);

        assertNotNull(result);
        assertEquals(originalInitialDate, result.initialDate());
        assertEquals(originalFinalDate, result.finalDate());
        assertTrue(result.isActive());
        assertEquals("2024", result.year());
        assertEquals("1", result.period());

        verify(requestPeriodValidator).updateRequestPeriod(any(RequestPeriodDTO.class));
        verify(requestPeriodRepository).save(any(RequestPeriodDTO.class));
    }

    @Test
    void updateActivePeriod_DeactivatingPeriod_ShouldSetFinalDateToToday() {
        RequestPeriodDTO existingPeriod = new RequestPeriodDTO(
                "123", LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), "2024", "1", true
        );

        UpdateRequestPeriodDTO updateDTO = new UpdateRequestPeriodDTO(null, null, false);

        when(requestPeriodRepository.activePeriod()).thenReturn(existingPeriod);
        when(requestPeriodRepository.save(any(RequestPeriodDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RequestPeriodDTO result = requestPeriodService.updateActivePeriod(updateDTO);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.finalDate());
        assertFalse(result.isActive());
        assertEquals("2024", result.year());
        assertEquals("1", result.period());

        verify(requestPeriodValidator).updateRequestPeriod(any(RequestPeriodDTO.class));
        verify(requestPeriodRepository).save(any(RequestPeriodDTO.class));
    }

    @Test
    void updateActivePeriod_WithSameValues_ShouldKeepOriginalValues() {
        LocalDate originalInitialDate = LocalDate.now().minusDays(10);
        LocalDate originalFinalDate = LocalDate.now().plusDays(10);

        RequestPeriodDTO existingPeriod = new RequestPeriodDTO(
                "123", originalInitialDate, originalFinalDate, "2024", "1", true
        );

        UpdateRequestPeriodDTO updateDTO = new UpdateRequestPeriodDTO(
                originalInitialDate, originalFinalDate, true
        );

        when(requestPeriodRepository.activePeriod()).thenReturn(existingPeriod);
        when(requestPeriodRepository.save(any(RequestPeriodDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RequestPeriodDTO result = requestPeriodService.updateActivePeriod(updateDTO);

        assertNotNull(result);
        assertEquals(originalInitialDate, result.initialDate());
        assertEquals(originalFinalDate, result.finalDate());
        assertTrue(result.isActive());
        assertEquals("2024", result.year());
        assertEquals("1", result.period());

        verify(requestPeriodValidator).updateRequestPeriod(any(RequestPeriodDTO.class));
        verify(requestPeriodRepository).save(any(RequestPeriodDTO.class));
    }

    @Test
    void updateActivePeriod_WithPartialUpdates_ShouldUpdateOnlyChangedFields() {
        LocalDate originalInitialDate = LocalDate.now().minusDays(10);
        LocalDate originalFinalDate = LocalDate.now().plusDays(10);

        RequestPeriodDTO existingPeriod = new RequestPeriodDTO(
                "123", originalInitialDate, originalFinalDate, "2024", "1", true
        );

        LocalDate newFinalDate = LocalDate.now().plusDays(20);
        UpdateRequestPeriodDTO updateDTO = new UpdateRequestPeriodDTO(null, newFinalDate, true);

        when(requestPeriodRepository.activePeriod()).thenReturn(existingPeriod);
        when(requestPeriodRepository.save(any(RequestPeriodDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RequestPeriodDTO result = requestPeriodService.updateActivePeriod(updateDTO);

        assertNotNull(result);
        assertEquals(originalInitialDate, result.initialDate());
        assertEquals(newFinalDate, result.finalDate());
        assertTrue(result.isActive());

        verify(requestPeriodValidator).updateRequestPeriod(any(RequestPeriodDTO.class));
        verify(requestPeriodRepository).save(any(RequestPeriodDTO.class));
    }
}