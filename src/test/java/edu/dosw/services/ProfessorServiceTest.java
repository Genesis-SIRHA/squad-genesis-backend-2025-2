package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.ProfessorDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Professor;
import edu.dosw.repositories.ProfessorRepository;
import edu.dosw.services.UserServices.ProfessorService;
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
class ProfessorServiceTest {

  @Mock private ProfessorRepository professorRepository;

  @Mock private IdGenerator idGenerator;

  @Mock private AuthenticationService authenticationService;

  @InjectMocks private ProfessorService professorService;

  private ProfessorDto validProfessorDto;
  private Professor existingProfessor;
  private final String PROFESSOR_ID = "prof-123";
  private final String GENERATED_ID = "generated-prof-456";
  private final String FACULTY_NAME = "Engineering";

  @BeforeEach
  void setUp() {
    validProfessorDto = new ProfessorDto("Juan Carlos Perez Gomez", "12345678", FACULTY_NAME);

    existingProfessor =
        new Professor.ProfessorBuilder()
            .userId(PROFESSOR_ID)
            .fullName("Juan Carlos Perez Gomez")
            .email("juan.perez-g@escuelaing.edu.co")
            .identityDocument("12345678")
            .facultyName(FACULTY_NAME)
            .build();
  }

  // Tests para generateProfessorEmail usando reflection
  @Test
  void generateProfessorEmail_WithValidThreePartName_ShouldGenerateCorrectEmail() throws Exception {
    Method method =
        ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(professorService, "Carlos Andres Lopez");

    assertEquals("carlos.andres-l@escuelaing.edu.co", result);
  }

  @Test
  void generateProfessorEmail_WithValidFourPartName_ShouldGenerateCorrectEmail() throws Exception {
    Method method =
        ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(professorService, "Ana Maria Rodriguez Sanchez");

    assertEquals("ana.rodriguez-s@escuelaing.edu.co", result);
  }

  @Test
  void generateProfessorEmail_WithUpperCaseName_ShouldGenerateLowerCaseEmail() throws Exception {
    Method method =
        ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(professorService, "PEDRO JOSE GARCIA");

    assertEquals("pedro.jose-g@escuelaing.edu.co", result);
  }

  @Test
  void generateProfessorEmail_WithTwoPartName_ShouldThrowValidationException() throws Exception {
    Method method =
        ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
    method.setAccessible(true);

    Exception exception =
        assertThrows(Exception.class, () -> method.invoke(professorService, "Nombre Apellido"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  @Test
  void generateProfessorEmail_WithSingleWordName_ShouldThrowValidationException() throws Exception {
    Method method =
        ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
    method.setAccessible(true);

    Exception exception =
        assertThrows(Exception.class, () -> method.invoke(professorService, "Unico"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  // Tests para getProfessorById
  @Test
  void getProfessorById_WhenProfessorExists_ShouldReturnProfessor() {
    when(professorRepository.findByUserId(PROFESSOR_ID)).thenReturn(Optional.of(existingProfessor));

    Professor result = professorService.getProfessorById(PROFESSOR_ID);

    assertNotNull(result);
    assertEquals(PROFESSOR_ID, result.getUserId());
    assertEquals("Juan Carlos Perez Gomez", result.getFullName());
    assertEquals(FACULTY_NAME, result.getFacultyName());
    verify(professorRepository, times(1)).findByUserId(PROFESSOR_ID);
  }

  @Test
  void getProfessorById_WhenProfessorNotFound_ShouldThrowException() {
    when(professorRepository.findByUserId(PROFESSOR_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> professorService.getProfessorById(PROFESSOR_ID));

    assertEquals("Professor not found by id: " + PROFESSOR_ID, exception.getMessage());
    verify(professorRepository, times(1)).findByUserId(PROFESSOR_ID);
  }

  @Test
  void deleteProfessor_WhenProfessorExists_ShouldDeleteSuccessfully() {
    when(professorRepository.findByUserId(PROFESSOR_ID)).thenReturn(Optional.of(existingProfessor));
    doNothing().when(authenticationService).deleteAuthentication(existingProfessor);
    doNothing().when(professorRepository).delete(existingProfessor);

    Professor result = professorService.deleteProfessor(PROFESSOR_ID);

    assertNotNull(result);
    assertEquals(PROFESSOR_ID, result.getUserId());
    verify(authenticationService, times(1)).deleteAuthentication(existingProfessor);
    verify(professorRepository, times(1)).delete(existingProfessor);
  }

  @Test
  void deleteProfessor_WhenRepositoryDeletionFails_ShouldThrowBusinessException() {
    when(professorRepository.findByUserId(PROFESSOR_ID)).thenReturn(Optional.of(existingProfessor));
    doNothing().when(authenticationService).deleteAuthentication(existingProfessor);
    doThrow(new RuntimeException("Database error"))
        .when(professorRepository)
        .delete(existingProfessor);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> professorService.deleteProfessor(PROFESSOR_ID));

    assertTrue(
        exception
            .getMessage()
            .contains("An inesperated error has occurred when deleting the professor"));
    verify(authenticationService, times(1)).deleteAuthentication(existingProfessor);
    verify(professorRepository, times(1)).delete(existingProfessor);
  }

  // Tests para getFacultyByProfessorId
  @Test
  void getFacultyByProfessorId_WhenProfessorExists_ShouldReturnFacultyName() {
    when(professorRepository.findByUserId(PROFESSOR_ID)).thenReturn(Optional.of(existingProfessor));

    String result = professorService.getFacultyByProfessorId(PROFESSOR_ID);

    assertEquals(FACULTY_NAME, result);
    verify(professorRepository, times(1)).findByUserId(PROFESSOR_ID);
  }

  @Test
  void getFacultyByProfessorId_WhenProfessorNotFound_ShouldThrowException() {
    when(professorRepository.findByUserId(PROFESSOR_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> professorService.getFacultyByProfessorId(PROFESSOR_ID));

    assertEquals("User not found with professorId: " + PROFESSOR_ID, exception.getMessage());
    verify(professorRepository, times(1)).findByUserId(PROFESSOR_ID);
  }

  @Test
  void getFacultyByProfessorId_WithDifferentProfessorId_ShouldReturnCorrectFaculty() {
    String differentProfessorId = "prof-999";
    Professor differentProfessor =
        new Professor.ProfessorBuilder()
            .userId(differentProfessorId)
            .fullName("Different Professor")
            .email("different@escuelaing.edu.co")
            .identityDocument("99999999")
            .facultyName("Different Faculty")
            .build();

    when(professorRepository.findByUserId(differentProfessorId))
        .thenReturn(Optional.of(differentProfessor));

    String result = professorService.getFacultyByProfessorId(differentProfessorId);

    assertEquals("Different Faculty", result);
    verify(professorRepository, times(1)).findByUserId(differentProfessorId);
  }
}
