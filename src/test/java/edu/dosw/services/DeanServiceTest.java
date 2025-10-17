package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

  // Tests para createDean
  @Test
  void createDean_WithValidData_ShouldCreateDeanSuccessfully() {
    when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
    when(deanRepository.save(any(Dean.class))).thenReturn(existingDean);
    doNothing().when(authenticationService).createAuthentication(any(Dean.class));

    Dean result = deanService.createDean(validDeanDto);

    assertNotNull(result);
    verify(idGenerator, times(1)).generateUniqueId();
    verify(authenticationService, times(1)).createAuthentication(any(Dean.class));
    verify(deanRepository, times(1)).save(any(Dean.class));
  }

  @Test
  void createDean_WithNullIdentityDocument_ShouldThrowValidationException() {
    DeanDto invalidDto = new DeanDto("Juan Carlos Perez Gomez", null, FACULTY_NAME);

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class, () -> deanService.createDean(invalidDto));

    assertEquals("Personal data is incomplete", exception.getMessage());
    verify(deanRepository, never()).save(any(Dean.class));
  }

  @Test
  void createDean_WithNullFullName_ShouldThrowValidationException() {
    DeanDto invalidDto = new DeanDto(null, "12345678", FACULTY_NAME);

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class, () -> deanService.createDean(invalidDto));

    assertEquals("Personal data is incomplete", exception.getMessage());
    verify(deanRepository, never()).save(any(Dean.class));
  }

  @Test
  void createDean_WithNullFacultyName_ShouldThrowValidationException() {
    DeanDto invalidDto = new DeanDto("Juan Carlos Perez Gomez", "12345678", null);

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class, () -> deanService.createDean(invalidDto));

    assertEquals("Academic data is incomplete", exception.getMessage());
    verify(deanRepository, never()).save(any(Dean.class));
  }

  @Test
  void createDean_WithAllNullFields_ShouldThrowValidationException() {
    DeanDto invalidDto = new DeanDto(null, null, null);

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class, () -> deanService.createDean(invalidDto));

    assertEquals("Personal data is incomplete", exception.getMessage());
    verify(deanRepository, never()).save(any(Dean.class));
  }

  @Test
  void createDean_WhenAuthenticationServiceThrowsException_ShouldThrowBusinessException() {
    when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
    doThrow(new RuntimeException("Authentication service error"))
        .when(authenticationService)
        .createAuthentication(any(Dean.class));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> deanService.createDean(validDeanDto));

    assertTrue(
        exception
            .getMessage()
            .contains("An inesperated error has occurred when creating the dean"));
    verify(deanRepository, never()).save(any(Dean.class));
  }

  @Test
  void createDean_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
    when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
    doNothing().when(authenticationService).createAuthentication(any(Dean.class));
    when(deanRepository.save(any(Dean.class))).thenThrow(new RuntimeException("Database error"));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> deanService.createDean(validDeanDto));

    assertTrue(
        exception
            .getMessage()
            .contains("An inesperated error has occurred when creating the dean"));
  }

  // Tests para updateDean
  @Test
  void updateDean_WhenDeanExists_ShouldUpdateAllFieldsSuccessfully() {
    DeanDto updateDto = new DeanDto("Juan Carlos Perez Garcia", "87654321", "Science");

    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    when(deanRepository.save(any(Dean.class))).thenReturn(existingDean);

    Dean result = deanService.updateDean(DEAN_ID, updateDto);

    assertNotNull(result);
    verify(deanRepository, times(1)).findByUserId(DEAN_ID);
    verify(deanRepository, times(1)).save(existingDean);
  }

  @Test
  void updateDean_WhenOnlyNameProvided_ShouldUpdateOnlyName() {
    DeanDto nameOnlyUpdateDto = new DeanDto("Nuevo Nombre Completo", null, null);

    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    when(deanRepository.save(any(Dean.class))).thenReturn(existingDean);

    Dean result = deanService.updateDean(DEAN_ID, nameOnlyUpdateDto);

    assertNotNull(result);
    assertEquals("Nuevo Nombre Completo", existingDean.getFullName());
    verify(deanRepository, times(1)).save(existingDean);
  }

  @Test
  void updateDean_WhenOnlyDocumentProvided_ShouldUpdateOnlyDocument() {
    DeanDto documentOnlyUpdateDto = new DeanDto(null, "99999999", null);

    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    when(deanRepository.save(any(Dean.class))).thenReturn(existingDean);

    Dean result = deanService.updateDean(DEAN_ID, documentOnlyUpdateDto);

    assertNotNull(result);
    assertEquals("99999999", existingDean.getIdentityDocument());
    verify(deanRepository, times(1)).save(existingDean);
  }

  @Test
  void updateDean_WhenOnlyFacultyProvided_ShouldUpdateOnlyFaculty() {
    DeanDto facultyOnlyUpdateDto = new DeanDto(null, null, "Arts");

    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    when(deanRepository.save(any(Dean.class))).thenReturn(existingDean);

    Dean result = deanService.updateDean(DEAN_ID, facultyOnlyUpdateDto);

    assertNotNull(result);
    assertEquals("Arts", existingDean.getFacultyName());
    verify(deanRepository, times(1)).save(existingDean);
  }

  @Test
  void updateDean_WithNoFieldsToUpdate_ShouldReturnSameDean() {
    DeanDto emptyUpdateDto = new DeanDto(null, null, null);

    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    when(deanRepository.save(any(Dean.class))).thenReturn(existingDean);

    Dean result = deanService.updateDean(DEAN_ID, emptyUpdateDto);

    assertNotNull(result);
    verify(deanRepository, times(1)).save(existingDean);
  }

  @Test
  void updateDean_WhenDeanNotFound_ShouldThrowException() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> deanService.updateDean(DEAN_ID, validDeanDto));

    assertEquals("Dean not found", exception.getMessage());
    verify(deanRepository, never()).save(any(Dean.class));
  }

  @Test
  void updateDean_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    when(deanRepository.save(any(Dean.class))).thenThrow(new RuntimeException("Database error"));

    BusinessException exception =
        assertThrows(BusinessException.class, () -> deanService.updateDean(DEAN_ID, validDeanDto));

    assertTrue(
        exception
            .getMessage()
            .contains("An inesperated error has occurred when updating the dean"));
    verify(deanRepository, times(1)).save(any(Dean.class));
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
  void deleteDean_WhenAuthenticationDeletionFails_ShouldThrowBusinessException() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    doThrow(new RuntimeException("Auth deletion failed"))
        .when(authenticationService)
        .deleteAuthentication(existingDean);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> deanService.deleteDean(DEAN_ID));

    assertTrue(
        exception
            .getMessage()
            .contains("An inesperated error has occurred when deleting the dean"));
    verify(deanRepository, never()).delete(any(Dean.class));
  }

  @Test
  void deleteDean_WhenDeanNotFound_ShouldThrowException() {
    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> deanService.deleteDean(DEAN_ID));

    assertEquals("Dean not found", exception.getMessage());
    verify(authenticationService, never()).deleteAuthentication(any(Dean.class));
    verify(deanRepository, never()).delete(any(Dean.class));
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
  void createDean_WithSpecialCharactersInName_ShouldGenerateValidEmail() {
    DeanDto deanWithSpecialChars = new DeanDto("José María Pérez", "99999999", FACULTY_NAME);

    when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
    when(deanRepository.save(any(Dean.class))).thenAnswer(invocation -> invocation.getArgument(0));
    doNothing().when(authenticationService).createAuthentication(any(Dean.class));

    Dean result = deanService.createDean(deanWithSpecialChars);

    assertNotNull(result);
    assertEquals("josé.pérez-m@escuelaing.edu.co", result.getEmail());
  }

  @Test
  void updateDean_VerifyAllFieldsCanBeUpdatedSimultaneously() {
    DeanDto updateDto = new DeanDto("New Full Name", "11111111", "New Faculty");

    when(deanRepository.findByUserId(DEAN_ID)).thenReturn(Optional.of(existingDean));
    when(deanRepository.save(any(Dean.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Dean result = deanService.updateDean(DEAN_ID, updateDto);

    assertNotNull(result);
    assertEquals("New Full Name", existingDean.getFullName());
    assertEquals("11111111", existingDean.getIdentityDocument());
    assertEquals("New Faculty", existingDean.getFacultyName());
    verify(deanRepository, times(1)).save(existingDean);
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
