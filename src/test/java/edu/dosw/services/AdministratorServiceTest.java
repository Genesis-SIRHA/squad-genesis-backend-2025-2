package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.dosw.dto.AdministratorDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceAlreadyExistsException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Administrator;
import edu.dosw.repositories.AdministratorRepository;
import edu.dosw.services.UserServices.AdministratorService;
import edu.dosw.utils.IdGenerator;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class AdministratorServiceTest {

  @Mock private AdministratorRepository administratorRepository;

  @Mock private IdGenerator idGenerator;

  @Mock private AuthenticationService authenticationService;

  @InjectMocks private AdministratorService administratorService;

  private AdministratorDto validAdministratorDto;
  private Administrator existingAdministrator;
  private final String ADMIN_ID = "admin-123";
  private final String GENERATED_ID = "generated-admin-456";

  @BeforeEach
  void setUp() {
    validAdministratorDto = new AdministratorDto("12345678", "Juan Carlos Perez Gomez");

    existingAdministrator =
        new Administrator.AdministratorBuilder()
            .userId(ADMIN_ID)
            .fullName("Juan Carlos Perez Gomez")
            .email("juan.perez-g@admin.escuelaing.edu.co")
            .identityDocument("12345678")
            .build();
  }

  @Test
  void generateAdministratorEmail_WithValidThreePartName_ShouldGenerateCorrectEmail()
      throws Exception {
    Method method =
        AdministratorService.class.getDeclaredMethod("generateAdministratorEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(administratorService, "Carlos Andres Lopez");

    assertEquals("carlos.andres-l@admin.escuelaing.edu.co", result);
  }

  @Test
  void generateAdministratorEmail_WithValidFourPartName_ShouldGenerateCorrectEmail()
      throws Exception {
    Method method =
        AdministratorService.class.getDeclaredMethod("generateAdministratorEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(administratorService, "Ana Maria Rodriguez Sanchez");

    assertEquals("ana.rodriguez-s@admin.escuelaing.edu.co", result);
  }

  @Test
  void generateAdministratorEmail_WithUpperCaseName_ShouldGenerateLowerCaseEmail()
      throws Exception {
    Method method =
        AdministratorService.class.getDeclaredMethod("generateAdministratorEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(administratorService, "PEDRO JOSE GARCIA");

    assertEquals("pedro.jose-g@admin.escuelaing.edu.co", result);
  }

  @Test
  void generateAdministratorEmail_WithTwoPartName_ShouldThrowValidationException()
      throws Exception {
    Method method =
        AdministratorService.class.getDeclaredMethod("generateAdministratorEmail", String.class);
    method.setAccessible(true);

    Exception exception =
        assertThrows(Exception.class, () -> method.invoke(administratorService, "Nombre Apellido"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  @Test
  void generateAdministratorEmail_WithSingleWordName_ShouldThrowValidationException()
      throws Exception {
    Method method =
        AdministratorService.class.getDeclaredMethod("generateAdministratorEmail", String.class);
    method.setAccessible(true);

    Exception exception =
        assertThrows(Exception.class, () -> method.invoke(administratorService, "Unico"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  @Test
  void createAdministrator_WithBothNullFields_ShouldThrowValidationException() {
    AdministratorDto invalidDto = new AdministratorDto(null, null);

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class,
            () -> administratorService.createAdministrator(invalidDto));

    assertEquals("Personal data is incomplete", exception.getMessage());
    verify(administratorRepository, never()).save(any(Administrator.class));
  }

  @Test
  void createAdministrator_WithEmptyStringDocument_ShouldThrowValidationException() {
    AdministratorDto invalidDto = new AdministratorDto("", "Juan Carlos Perez Gomez");

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class,
            () -> administratorService.createAdministrator(invalidDto));

    assertTrue(exception.getMessage().contains("Invalid full name"));
    verify(administratorRepository, never()).save(any(Administrator.class));
  }

  @Test
  void createAdministrator_WithBlankSpacesDocument_ShouldThrowValidationException() {
    AdministratorDto invalidDto = new AdministratorDto("   ", "Juan Carlos Perez Gomez");

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class,
            () -> administratorService.createAdministrator(invalidDto));

    assertTrue(exception.getMessage().contains("Invalid full name"));
    verify(administratorRepository, never()).save(any(Administrator.class));
  }

  @Test
  void createAdministrator_WithBlankSpacesName_ShouldThrowValidationException() {
    AdministratorDto invalidDto = new AdministratorDto("12345678", "   ");

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class,
            () -> administratorService.createAdministrator(invalidDto));

    assertTrue(exception.getMessage().contains("Invalid full name"));
    verify(administratorRepository, never()).save(any(Administrator.class));
  }

  @Test
  void createAdministrator_WithEmptyStringName_ShouldThrowValidationException() {
    AdministratorDto invalidDto = new AdministratorDto("12345678", "");

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class,
            () -> administratorService.createAdministrator(invalidDto));

    assertTrue(exception.getMessage().contains("Invalid full name"));
    verify(administratorRepository, never()).save(any(Administrator.class));
  }

  @Test
  void getAdministratorById_WhenAdministratorExists_ShouldReturnAdministrator() {
    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));

    Administrator result = administratorService.getAdministratorById(ADMIN_ID);

    assertNotNull(result);
    assertEquals(ADMIN_ID, result.getUserId());
    assertEquals("Juan Carlos Perez Gomez", result.getFullName());
    verify(administratorRepository, times(1)).findByUserId(ADMIN_ID);
  }

  @Test
  void getAdministratorById_WhenAdministratorNotFound_ShouldThrowException() {
    when(administratorRepository.findByUserId(ADMIN_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> administratorService.getAdministratorById(ADMIN_ID));

    assertEquals("Administrator not found by id: " + ADMIN_ID, exception.getMessage());
    verify(administratorRepository, times(1)).findByUserId(ADMIN_ID);
  }

  @Test
  void updateAdministrator_WhenAdministratorExists_ShouldUpdateAllFieldsSuccessfully() {
    AdministratorDto updateDto = new AdministratorDto("87654321", "Juan Carlos Perez Garcia");

    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class))).thenReturn(existingAdministrator);

    Administrator result = administratorService.updateAdministrator(ADMIN_ID, updateDto);

    assertNotNull(result);
    verify(administratorRepository, times(1)).findByUserId(ADMIN_ID);
    verify(administratorRepository, times(1)).save(existingAdministrator);
  }

  @Test
  void updateAdministrator_WhenOnlyNameProvided_ShouldUpdateOnlyName() {
    AdministratorDto nameOnlyUpdateDto = new AdministratorDto(null, "Nuevo Nombre Completo");

    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class))).thenReturn(existingAdministrator);

    Administrator result = administratorService.updateAdministrator(ADMIN_ID, nameOnlyUpdateDto);

    assertNotNull(result);
    verify(administratorRepository, times(1)).save(existingAdministrator);
  }

  @Test
  void updateAdministrator_WhenOnlyDocumentProvided_ShouldUpdateOnlyDocument() {
    AdministratorDto documentOnlyUpdateDto = new AdministratorDto("99999999", null);

    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class))).thenReturn(existingAdministrator);

    Administrator result =
        administratorService.updateAdministrator(ADMIN_ID, documentOnlyUpdateDto);

    assertNotNull(result);
    verify(administratorRepository, times(1)).save(existingAdministrator);
  }

  @Test
  void updateAdministrator_WithNoFieldsToUpdate_ShouldReturnSameAdministrator() {
    AdministratorDto emptyUpdateDto = new AdministratorDto(null, null);

    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class))).thenReturn(existingAdministrator);

    Administrator result = administratorService.updateAdministrator(ADMIN_ID, emptyUpdateDto);

    assertNotNull(result);
    verify(administratorRepository, times(1)).save(existingAdministrator);
  }

  @Test
  void updateAdministrator_WhenAdministratorNotFound_ShouldThrowException() {
    when(administratorRepository.findByUserId(ADMIN_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> administratorService.updateAdministrator(ADMIN_ID, validAdministratorDto));

    assertEquals("Administrator not found", exception.getMessage());
    verify(administratorRepository, never()).save(any(Administrator.class));
  }

  @Test
  void updateAdministrator_WithDataIntegrityViolation_ShouldThrowException() {
    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class)))
        .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

    ResourceAlreadyExistsException exception =
        assertThrows(
            ResourceAlreadyExistsException.class,
            () -> administratorService.updateAdministrator(ADMIN_ID, validAdministratorDto));

    assertTrue(exception.getMessage().contains("Data integrity violation during update"));
    verify(administratorRepository, times(1)).save(any(Administrator.class));
  }

  @Test
  void updateAdministrator_WithUnexpectedError_ShouldThrowBusinessException() {
    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class)))
        .thenThrow(new RuntimeException("Unexpected database error"));

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> administratorService.updateAdministrator(ADMIN_ID, validAdministratorDto));

    assertEquals(
        "An inesperated error has occurred when updating the administrator",
        exception.getMessage());
    verify(administratorRepository, times(1)).save(any(Administrator.class));
  }

  @Test
  void deleteAdministrator_WhenAdministratorExists_ShouldDeleteSuccessfully() {
    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    doNothing().when(authenticationService).deleteAuthentication(existingAdministrator);
    doNothing().when(administratorRepository).delete(existingAdministrator);

    Administrator result = administratorService.deleteAdministrator(ADMIN_ID);

    assertNotNull(result);
    assertEquals(ADMIN_ID, result.getUserId());
    verify(authenticationService, times(1)).deleteAuthentication(existingAdministrator);
    verify(administratorRepository, times(1)).delete(existingAdministrator);
  }

  @Test
  void deleteAdministrator_WhenAuthenticationDeletionFails_ShouldThrowBusinessException() {
    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    doThrow(new RuntimeException("Auth deletion failed"))
        .when(authenticationService)
        .deleteAuthentication(existingAdministrator);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> administratorService.deleteAdministrator(ADMIN_ID));

    assertEquals(
        "An inesperated error has occurred when deleting the administrator",
        exception.getMessage());
    verify(administratorRepository, never()).delete(any(Administrator.class));
  }

  @Test
  void deleteAdministrator_WhenAdministratorNotFound_ShouldThrowException() {
    when(administratorRepository.findByUserId(ADMIN_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> administratorService.deleteAdministrator(ADMIN_ID));

    assertEquals("Administrator not found", exception.getMessage());
    verify(authenticationService, never()).deleteAuthentication(any(Administrator.class));
    verify(administratorRepository, never()).delete(any(Administrator.class));
  }

  @Test
  void deleteAdministrator_WithUnexpectedError_ShouldThrowBusinessException() {
    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    doNothing().when(authenticationService).deleteAuthentication(existingAdministrator);
    doThrow(new RuntimeException("Database error"))
        .when(administratorRepository)
        .delete(existingAdministrator);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> administratorService.deleteAdministrator(ADMIN_ID));

    assertEquals(
        "An inesperated error has occurred when deleting the administrator",
        exception.getMessage());
    verify(authenticationService, times(1)).deleteAuthentication(existingAdministrator);
    verify(administratorRepository, times(1)).delete(existingAdministrator);
  }

  @Test
  void updateAdministrator_WithEmptyStringValues_ShouldHandleGracefully() {
    AdministratorDto updateDto = new AdministratorDto("", "");

    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class))).thenReturn(existingAdministrator);

    Administrator result = administratorService.updateAdministrator(ADMIN_ID, updateDto);

    assertNotNull(result);
    verify(administratorRepository, times(1)).save(existingAdministrator);
  }

  @Test
  void updateAdministrator_WithBlankSpaceValues_ShouldHandleGracefully() {
    AdministratorDto updateDto = new AdministratorDto("   ", "   ");

    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenReturn(Optional.of(existingAdministrator));
    when(administratorRepository.save(any(Administrator.class))).thenReturn(existingAdministrator);

    Administrator result = administratorService.updateAdministrator(ADMIN_ID, updateDto);

    assertNotNull(result);
    verify(administratorRepository, times(1)).save(existingAdministrator);
  }

  @Test
  void getAdministratorById_WithSpecialCharacters_ShouldHandleCorrectly() {
    String specialId = "admin-123-!@#$";
    when(administratorRepository.findByUserId(specialId))
        .thenReturn(Optional.of(existingAdministrator));

    Administrator result = administratorService.getAdministratorById(specialId);

    assertNotNull(result);
    verify(administratorRepository, times(1)).findByUserId(specialId);
  }

  @Test
  void getAdministratorById_WhenRepositoryThrowsException_ShouldPropagate() {
    when(administratorRepository.findByUserId(ADMIN_ID))
        .thenThrow(new RuntimeException("Database error"));

    assertThrows(RuntimeException.class, () -> administratorService.getAdministratorById(ADMIN_ID));

    verify(administratorRepository, times(1)).findByUserId(ADMIN_ID);
  }

  @Test
  void createAdministrator_WithNullIdentityDocument_ShouldThrowValidationException() {
    AdministratorDto invalidDto = new AdministratorDto("Juan Carlos Perez Gomez", null);

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class,
            () -> administratorService.createAdministrator(invalidDto));

    assertEquals("Personal data is incomplete", exception.getMessage());
    verify(administratorRepository, never()).save(any(Administrator.class));
  }

  @Test
  void createAdministrator_WithNullFullName_ShouldThrowValidationException() {
    AdministratorDto invalidDto = new AdministratorDto(null, "12345678");

    jakarta.validation.ValidationException exception =
        assertThrows(
            jakarta.validation.ValidationException.class,
            () -> administratorService.createAdministrator(invalidDto));

    assertEquals("Personal data is incomplete", exception.getMessage());
    verify(administratorRepository, never()).save(any(Administrator.class));
  }
}
