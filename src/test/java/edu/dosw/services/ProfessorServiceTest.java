package edu.dosw.services;

import edu.dosw.dto.ProfessorDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.exception.ResourceAlreadyExistsException;
import edu.dosw.model.Professor;
import edu.dosw.repositories.ProfessorRepository;
import edu.dosw.services.AuthenticationService;
import edu.dosw.services.UserServices.ProfessorService;
import edu.dosw.utils.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private ProfessorService professorService;

    private ProfessorDto validProfessorDto;
    private Professor existingProfessor;
    private final String PROFESSOR_ID = "prof-123";
    private final String GENERATED_ID = "generated-prof-456";
    private final String FACULTY_NAME = "Engineering";

    @BeforeEach
    void setUp() {
        validProfessorDto = new ProfessorDto(
                "Juan Carlos Perez Gomez",
                "12345678",
                FACULTY_NAME
        );

        existingProfessor = new Professor.ProfessorBuilder()
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
        Method method = ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(professorService, "Carlos Andres Lopez");

        assertEquals("carlos.andres-l@escuelaing.edu.co", result);
    }

    @Test
    void generateProfessorEmail_WithValidFourPartName_ShouldGenerateCorrectEmail() throws Exception {
        Method method = ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(professorService, "Ana Maria Rodriguez Sanchez");

        assertEquals("ana.rodriguez-s@escuelaing.edu.co", result);
    }

    @Test
    void generateProfessorEmail_WithUpperCaseName_ShouldGenerateLowerCaseEmail() throws Exception {
        Method method = ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(professorService, "PEDRO JOSE GARCIA");

        assertEquals("pedro.jose-g@escuelaing.edu.co", result);
    }

    @Test
    void generateProfessorEmail_WithTwoPartName_ShouldThrowValidationException() throws Exception {
        Method method = ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(professorService, "Nombre Apellido"));

        assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
    }

    @Test
    void generateProfessorEmail_WithSingleWordName_ShouldThrowValidationException() throws Exception {
        Method method = ProfessorService.class.getDeclaredMethod("generateProfessorEmail", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(professorService, "Unico"));

        assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
    }

    // Tests para getProfessorById
    @Test
    void getProfessorById_WhenProfessorExists_ShouldReturnProfessor() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));

        Professor result = professorService.getProfessorById(PROFESSOR_ID);

        assertNotNull(result);
        assertEquals(PROFESSOR_ID, result.getUserId());
        assertEquals("Juan Carlos Perez Gomez", result.getFullName());
        assertEquals(FACULTY_NAME, result.getFacultyName());
        verify(professorRepository, times(1)).findByUserId(PROFESSOR_ID);
    }

    @Test
    void getProfessorById_WhenProfessorNotFound_ShouldThrowException() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> professorService.getProfessorById(PROFESSOR_ID)
        );

        assertEquals("Professor not found by id: " + PROFESSOR_ID, exception.getMessage());
        verify(professorRepository, times(1)).findByUserId(PROFESSOR_ID);
    }

    // Tests para createProfessor
    @Test
    void createProfessor_WithValidData_ShouldCreateProfessorSuccessfully() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(professorRepository.save(any(Professor.class)))
                .thenReturn(existingProfessor);
        doNothing().when(authenticationService).createAuthentication(any(Professor.class));

        Professor result = professorService.createProfessor(validProfessorDto);

        assertNotNull(result);
        verify(idGenerator, times(1)).generateUniqueId();
        verify(authenticationService, times(1)).createAuthentication(any(Professor.class));
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    @Test
    void createProfessor_WithNullIdentityDocument_ShouldThrowValidationException() {
        ProfessorDto invalidDto = new ProfessorDto("Juan Carlos Perez Gomez", null, FACULTY_NAME);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> professorService.createProfessor(invalidDto)
        );

        assertEquals("Personal data is incomplete", exception.getMessage());
        verify(professorRepository, never()).save(any(Professor.class));
    }

    @Test
    void createProfessor_WithNullFullName_ShouldThrowValidationException() {
        ProfessorDto invalidDto = new ProfessorDto(null, "12345678", FACULTY_NAME);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> professorService.createProfessor(invalidDto)
        );

        assertEquals("Personal data is incomplete", exception.getMessage());
        verify(professorRepository, never()).save(any(Professor.class));
    }

    @Test
    void createProfessor_WithNullFacultyName_ShouldThrowValidationException() {
        ProfessorDto invalidDto = new ProfessorDto("Juan Carlos Perez Gomez", "12345678", null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> professorService.createProfessor(invalidDto)
        );

        assertEquals("Academic data is incomplete", exception.getMessage());
        verify(professorRepository, never()).save(any(Professor.class));
    }

    @Test
    void createProfessor_WithAllNullFields_ShouldThrowValidationException() {
        ProfessorDto invalidDto = new ProfessorDto(null, null, null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> professorService.createProfessor(invalidDto)
        );

        assertEquals("Personal data is incomplete", exception.getMessage());
        verify(professorRepository, never()).save(any(Professor.class));
    }

    @Test
    void createProfessor_WhenAuthenticationServiceThrowsException_ShouldThrowBusinessException() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        doThrow(new RuntimeException("Authentication service error"))
                .when(authenticationService).createAuthentication(any(Professor.class));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> professorService.createProfessor(validProfessorDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when creating the professor"));
        verify(professorRepository, never()).save(any(Professor.class));
    }

    @Test
    void createProfessor_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        doNothing().when(authenticationService).createAuthentication(any(Professor.class));
        when(professorRepository.save(any(Professor.class)))
                .thenThrow(new RuntimeException("Database error"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> professorService.createProfessor(validProfessorDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when creating the professor"));
    }

    @Test
    void createProfessor_WithInvalidNameForEmailGeneration_ShouldThrowValidationException() {
        ProfessorDto invalidNameDto = new ProfessorDto("Nombre", "12345678", FACULTY_NAME);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> professorService.createProfessor(invalidNameDto)
        );

        assertTrue(exception.getMessage().contains("Invalid full name"));
        verify(professorRepository, never()).save(any(Professor.class));
        verify(authenticationService, never()).createAuthentication(any(Professor.class));
    }

    // Tests para updateProfessor
    @Test
    void updateProfessor_WhenProfessorExists_ShouldUpdateAllFieldsSuccessfully() {
        ProfessorDto updateDto = new ProfessorDto("Juan Carlos Perez Garcia", "87654321", "Science");

        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        when(professorRepository.save(any(Professor.class)))
                .thenReturn(existingProfessor);

        Professor result = professorService.updateProfessor(PROFESSOR_ID, updateDto);

        assertNotNull(result);
        verify(professorRepository, times(1)).findByUserId(PROFESSOR_ID);
        verify(professorRepository, times(1)).save(existingProfessor);
    }

    @Test
    void updateProfessor_WhenOnlyNameProvided_ShouldUpdateOnlyName() {
        ProfessorDto nameOnlyUpdateDto = new ProfessorDto("Nuevo Nombre Completo", null, null);

        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        when(professorRepository.save(any(Professor.class)))
                .thenReturn(existingProfessor);

        Professor result = professorService.updateProfessor(PROFESSOR_ID, nameOnlyUpdateDto);

        assertNotNull(result);
        assertEquals("Nuevo Nombre Completo", existingProfessor.getFullName());
        verify(professorRepository, times(1)).save(existingProfessor);
    }

    @Test
    void updateProfessor_WhenOnlyDocumentProvided_ShouldUpdateOnlyDocument() {
        ProfessorDto documentOnlyUpdateDto = new ProfessorDto(null, "99999999", null);

        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        when(professorRepository.save(any(Professor.class)))
                .thenReturn(existingProfessor);

        Professor result = professorService.updateProfessor(PROFESSOR_ID, documentOnlyUpdateDto);

        assertNotNull(result);
        assertEquals("99999999", existingProfessor.getIdentityDocument());
        verify(professorRepository, times(1)).save(existingProfessor);
    }

    @Test
    void updateProfessor_WhenOnlyFacultyProvided_ShouldUpdateOnlyFaculty() {
        ProfessorDto facultyOnlyUpdateDto = new ProfessorDto(null, null, "Arts");

        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        when(professorRepository.save(any(Professor.class)))
                .thenReturn(existingProfessor);

        Professor result = professorService.updateProfessor(PROFESSOR_ID, facultyOnlyUpdateDto);

        assertNotNull(result);
        assertEquals("Arts", existingProfessor.getFacultyName());
        verify(professorRepository, times(1)).save(existingProfessor);
    }

    @Test
    void updateProfessor_WithNoFieldsToUpdate_ShouldReturnSameProfessor() {
        ProfessorDto emptyUpdateDto = new ProfessorDto(null, null, null);

        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        when(professorRepository.save(any(Professor.class)))
                .thenReturn(existingProfessor);

        Professor result = professorService.updateProfessor(PROFESSOR_ID, emptyUpdateDto);

        assertNotNull(result);
        verify(professorRepository, times(1)).save(existingProfessor);
    }

    @Test
    void updateProfessor_WhenProfessorNotFound_ShouldThrowException() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> professorService.updateProfessor(PROFESSOR_ID, validProfessorDto)
        );

        assertEquals("Professor not found", exception.getMessage());
        verify(professorRepository, never()).save(any(Professor.class));
    }

    @Test
    void updateProfessor_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        when(professorRepository.save(any(Professor.class)))
                .thenThrow(new RuntimeException("Database error"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> professorService.updateProfessor(PROFESSOR_ID, validProfessorDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when updating the professor"));
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    // Tests para deleteProfessor
    @Test
    void deleteProfessor_WhenProfessorExists_ShouldDeleteSuccessfully() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        doNothing().when(authenticationService).deleteAuthentication(existingProfessor);
        doNothing().when(professorRepository).delete(existingProfessor);

        Professor result = professorService.deleteProfessor(PROFESSOR_ID);

        assertNotNull(result);
        assertEquals(PROFESSOR_ID, result.getUserId());
        verify(authenticationService, times(1)).deleteAuthentication(existingProfessor);
        verify(professorRepository, times(1)).delete(existingProfessor);
    }

    @Test
    void deleteProfessor_WhenAuthenticationDeletionFails_ShouldThrowBusinessException() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        doThrow(new RuntimeException("Auth deletion failed"))
                .when(authenticationService).deleteAuthentication(existingProfessor);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> professorService.deleteProfessor(PROFESSOR_ID)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when deleting the professor"));
        verify(professorRepository, never()).delete(any(Professor.class));
    }

    @Test
    void deleteProfessor_WhenProfessorNotFound_ShouldThrowException() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> professorService.deleteProfessor(PROFESSOR_ID)
        );

        assertEquals("Professor not found", exception.getMessage());
        verify(authenticationService, never()).deleteAuthentication(any(Professor.class));
        verify(professorRepository, never()).delete(any(Professor.class));
    }

    @Test
    void deleteProfessor_WhenRepositoryDeletionFails_ShouldThrowBusinessException() {
        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        doNothing().when(authenticationService).deleteAuthentication(existingProfessor);
        doThrow(new RuntimeException("Database error")).when(professorRepository).delete(existingProfessor);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> professorService.deleteProfessor(PROFESSOR_ID)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when deleting the professor"));
        verify(authenticationService, times(1)).deleteAuthentication(existingProfessor);
        verify(professorRepository, times(1)).delete(existingProfessor);
    }

    // Tests para getFacultyByProfessorId
    @Test
    void getFacultyByProfessorId_WhenProfessorExists_ShouldReturnFacultyName() {
        when(professorRepository.findById(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));

        String result = professorService.getFacultyByProfessorId(PROFESSOR_ID);

        assertEquals(FACULTY_NAME, result);
        verify(professorRepository, times(1)).findById(PROFESSOR_ID);
    }

    @Test
    void getFacultyByProfessorId_WhenProfessorNotFound_ShouldThrowException() {
        when(professorRepository.findById(PROFESSOR_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> professorService.getFacultyByProfessorId(PROFESSOR_ID)
        );

        assertEquals("User not found with professorId: " + PROFESSOR_ID, exception.getMessage());
        verify(professorRepository, times(1)).findById(PROFESSOR_ID);
    }


    @Test
    void createProfessor_WithSpecialCharactersInName_ShouldGenerateValidEmail() {
        ProfessorDto professorWithSpecialChars = new ProfessorDto(
                "José María Pérez",
                "99999999",
                FACULTY_NAME
        );

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(professorRepository.save(any(Professor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Professor.class));

        Professor result = professorService.createProfessor(professorWithSpecialChars);

        assertNotNull(result);
        assertEquals("josé.maría-p@escuelaing.edu.co", result.getEmail());
    }

    @Test
    void updateProfessor_VerifyAllFieldsCanBeUpdatedSimultaneously() {
        ProfessorDto updateDto = new ProfessorDto("New Full Name", "11111111", "New Faculty");

        when(professorRepository.findByUserId(PROFESSOR_ID))
                .thenReturn(Optional.of(existingProfessor));
        when(professorRepository.save(any(Professor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Professor result = professorService.updateProfessor(PROFESSOR_ID, updateDto);

        assertNotNull(result);
        assertEquals("New Full Name", existingProfessor.getFullName());
        assertEquals("11111111", existingProfessor.getIdentityDocument());
        assertEquals("New Faculty", existingProfessor.getFacultyName());
        verify(professorRepository, times(1)).save(existingProfessor);
    }

    @Test
    void getFacultyByProfessorId_WithDifferentProfessorId_ShouldReturnCorrectFaculty() {
        String differentProfessorId = "prof-999";
        Professor differentProfessor = new Professor.ProfessorBuilder()
                .userId(differentProfessorId)
                .fullName("Different Professor")
                .email("different@escuelaing.edu.co")
                .identityDocument("99999999")
                .facultyName("Different Faculty")
                .build();

        when(professorRepository.findById(differentProfessorId))
                .thenReturn(Optional.of(differentProfessor));

        String result = professorService.getFacultyByProfessorId(differentProfessorId);

        assertEquals("Different Faculty", result);
        verify(professorRepository, times(1)).findById(differentProfessorId);
    }

    @Test
    void createProfessor_VerifyProfessorBuilderUsage() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(professorRepository.save(any(Professor.class)))
                .thenAnswer(invocation -> {
                    Professor professor = invocation.getArgument(0);
                    assertEquals(GENERATED_ID, professor.getUserId());
                    assertEquals("Juan Carlos Perez Gomez", professor.getFullName());
                    assertEquals("12345678", professor.getIdentityDocument());
                    assertEquals(FACULTY_NAME, professor.getFacultyName());
                    assertEquals("juan.perez-g@escuelaing.edu.co", professor.getEmail());
                    return professor;
                });
        doNothing().when(authenticationService).createAuthentication(any(Professor.class));

        Professor result = professorService.createProfessor(validProfessorDto);

        assertNotNull(result);
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    @Test
    void createProfessor_WithMinimumLengthName_ShouldCreateSuccessfully() {
        ProfessorDto professorWithThreePartName = new ProfessorDto(
                "Ana Maria Lopez",
                "11111111",
                FACULTY_NAME
        );

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(professorRepository.save(any(Professor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Professor.class));

        Professor result = professorService.createProfessor(professorWithThreePartName);

        assertNotNull(result);
        assertEquals("ana.maria-l@escuelaing.edu.co", result.getEmail());
    }
}