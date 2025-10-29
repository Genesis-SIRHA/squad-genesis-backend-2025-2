package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.DeanDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Dean;
import edu.dosw.repositories.DeanRepository;
import edu.dosw.services.UserServices.DeanService;
import edu.dosw.utils.IdGenerator;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeanServiceTest {

  @Mock private DeanRepository deanRepository;

  @Mock private IdGenerator idGenerator;

  @Mock private AuthenticationService authenticationService;

  @InjectMocks private DeanService deanService;

  private DeanDto validDeanDto;
  private Dean existingDean;
  private final String DEAN_ID = "dean-123";
  private final String GENERATED_ID = "generated-dean-456";
  private final String FACULTY_NAME = "Engineering";

  @BeforeEach
  void setUp() {
    validDeanDto = new DeanDto("Juan Carlos Perez Gomez", "12345678", FACULTY_NAME);

    existingDean =
        new Dean.DeanBuilder()
            .userId(DEAN_ID)
            .fullName("Juan Carlos Perez Gomez")
            .email("juan.perez-g@escuelaing.edu.co")
            .identityDocument("12345678")
            .facultyName(FACULTY_NAME)
            .build();
  }

  // Tests para generateDeanEmail usando reflection
  @Test
  void generateDeanEmail_WithValidThreePartName_ShouldGenerateCorrectEmail() throws Exception {
    Method method = DeanService.class.getDeclaredMethod("generateDeanEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(deanService, "Carlos Andres Lopez");

    assertEquals("carlos.lopez-a@escuelaing.edu.co", result);
  }

  @Test
  void generateDeanEmail_WithValidFourPartName_ShouldGenerateCorrectEmail() throws Exception {
    Method method = DeanService.class.getDeclaredMethod("generateDeanEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(deanService, "Ana Maria Rodriguez Sanchez");

    assertEquals("ana.sanchez-r@escuelaing.edu.co", result);
  }

  @Test
  void generateDeanEmail_WithUpperCaseName_ShouldGenerateLowerCaseEmail() throws Exception {
    Method method = DeanService.class.getDeclaredMethod("generateDeanEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(deanService, "PEDRO JOSE GARCIA");

    assertEquals("pedro.garcia-j@escuelaing.edu.co", result);
  }

  @Test
  void generateDeanEmail_WithTwoPartName_ShouldThrowValidationException() throws Exception {
    Method method = DeanService.class.getDeclaredMethod("generateDeanEmail", String.class);
    method.setAccessible(true);

    Exception exception =
        assertThrows(Exception.class, () -> method.invoke(deanService, "Nombre Apellido"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  @Test
  void generateDeanEmail_WithSingleWordName_ShouldThrowValidationException() throws Exception {
    Method method = DeanService.class.getDeclaredMethod("generateDeanEmail", String.class);
    method.setAccessible(true);

    Exception exception = assertThrows(Exception.class, () -> method.invoke(deanService, "Unico"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  // Tests para getDeanById
  @Test
  void getDeanById_WhenDeanExists_ShouldReturnDean() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));

    Dean result = deanService.getDeanById(DEAN_ID);

    assertNotNull(result);
    assertEquals(DEAN_ID, result.getUserId());
    assertEquals("Juan Carlos Perez Gomez", result.getFullName());
    assertEquals(FACULTY_NAME, result.getFacultyName());
    verify(deanRepository, times(1)).findByUserId(DEAN_ID);
  }

  @Test
  void getDeanById_WhenDeanNotFound_ShouldThrowException() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> deanService.getDeanById(DEAN_ID));

    assertEquals("Dean not found by id: " + DEAN_ID, exception.getMessage());
    verify(deanRepository, times(1)).findByUserId(DEAN_ID);
  }

  // Tests para deleteDean
  @Test
  void deleteDean_WhenDeanExists_ShouldDeleteSuccessfully() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    doNothing().when(authenticationService).deleteAuthentication(existingDean);
    doNothing().when(deanRepository).delete(existingDean);

    Dean result = deanService.deleteDean(DEAN_ID);

    assertNotNull(result);
    assertEquals(DEAN_ID, result.getUserId());
    verify(authenticationService, times(1)).deleteAuthentication(existingDean);
    verify(deanRepository, times(1)).delete(existingDean);
  }

  @Test
  void deleteDean_WhenRepositoryDeletionFails_ShouldThrowBusinessException() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    doNothing().when(authenticationService).deleteAuthentication(existingDean);
    doThrow(new RuntimeException("Database error")).when(deanRepository).delete(existingDean);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> deanService.deleteDean(DEAN_ID));

    assertTrue(
        exception
            .getMessage()
            .contains("An inesperated error has occurred when deleting the dean"));
    verify(authenticationService, times(1)).deleteAuthentication(existingDean);
    verify(deanRepository, times(1)).delete(existingDean);
  }

  // Tests para getFacultyByDeanId
  @Test
  void getFacultyByDeanId_WhenDeanExists_ShouldReturnFacultyName() {
    when(deanRepository.findById(DEAN_ID)).thenReturn(Optional.of(existingDean));

    String result = deanService.getFacultyByDeanId(DEAN_ID);

    assertEquals(FACULTY_NAME, result);
    verify(deanRepository, times(1)).findById(DEAN_ID);
  }

  @Test
  void getFacultyByDeanId_WhenDeanNotFound_ShouldThrowException() {
    when(deanRepository.findById(DEAN_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> deanService.getFacultyByDeanId(DEAN_ID));

    assertEquals("User not found with deanId: " + DEAN_ID, exception.getMessage());
    verify(deanRepository, times(1)).findById(DEAN_ID);
  }

  @Test
  void getFacultyByDeanId_WithDifferentDeanId_ShouldReturnCorrectFaculty() {
    String differentDeanId = "dean-999";
    Dean differentDean =
        new Dean.DeanBuilder()
            .userId(differentDeanId)
            .fullName("Different Dean")
            .email("different@escuelaing.edu.co")
            .identityDocument("99999999")
            .facultyName("Different Faculty")
            .build();

    when(deanRepository.findById(differentDeanId)).thenReturn(Optional.of(differentDean));

    String result = deanService.getFacultyByDeanId(differentDeanId);

    assertEquals("Different Faculty", result);
    verify(deanRepository, times(1)).findById(differentDeanId);
  }
}
