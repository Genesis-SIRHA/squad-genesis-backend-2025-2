package edu.dosw.services;

import edu.dosw.dto.StudentDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Student;
import edu.dosw.model.enums.AcademicGrade;
import edu.dosw.repositories.StudentRepository;
import edu.dosw.services.AuthenticationService;
import edu.dosw.services.UserServices.StudentService;
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
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private StudentService studentService;

    private StudentDto validStudentDto;
    private Student existingStudent;
    private final String STUDENT_ID = "student-123";
    private final String GENERATED_ID = "generated-student-456";
    private final String FACULTY_NAME = "Engineering";
    private final String PLAN = "Software Engineering";

    @BeforeEach
    void setUp() {
        validStudentDto = new StudentDto(
                "12345678",
                "Juan Carlos Perez Gomez",
                PLAN,
                FACULTY_NAME,
                AcademicGrade.UNDERGRADUATE
        );

        existingStudent = new Student.StudentBuilder()
                .userId(STUDENT_ID)
                .fullName("Juan Carlos Perez Gomez")
                .email("juan.perez-g@mail.escuelaing.edu.co")
                .identityDocument("12345678")
                .plan(PLAN)
                .facultyName(FACULTY_NAME)
                .generalAverage(0)
                .academicGrade(AcademicGrade.UNDERGRADUATE)
                .build();
    }

    @Test
    void generateStudentEmail_WithValidThreePartName_ShouldGenerateCorrectEmail() throws Exception {
        Method method = StudentService.class.getDeclaredMethod("generateStudentEmail", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(studentService, "Carlos Andres Lopez");

        assertEquals("carlos.andres-l@mail.escuelaing.edu.co", result);
    }

    @Test
    void generateStudentEmail_WithValidFourPartName_ShouldGenerateCorrectEmail() throws Exception {
        Method method = StudentService.class.getDeclaredMethod("generateStudentEmail", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(studentService, "Ana Maria Rodriguez Sanchez");

        assertEquals("ana.rodriguez-s@mail.escuelaing.edu.co", result);
    }

    @Test
    void generateStudentEmail_WithUpperCaseName_ShouldGenerateLowerCaseEmail() throws Exception {
        Method method = StudentService.class.getDeclaredMethod("generateStudentEmail", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(studentService, "PEDRO JOSE GARCIA");

        assertEquals("pedro.jose-g@mail.escuelaing.edu.co", result);
    }

    @Test
    void generateStudentEmail_WithTwoPartName_ShouldThrowValidationException() throws Exception {
        Method method = StudentService.class.getDeclaredMethod("generateStudentEmail", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(studentService, "Nombre Apellido"));

        assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
    }

    @Test
    void generateStudentEmail_WithSingleWordName_ShouldThrowValidationException() throws Exception {
        Method method = StudentService.class.getDeclaredMethod("generateStudentEmail", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(studentService, "Unico"));

        assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
    }

    @Test
    void getStudentById_WhenStudentExists_ShouldReturnStudent() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));

        Student result = studentService.getStudentById(STUDENT_ID);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getUserId());
        assertEquals("Juan Carlos Perez Gomez", result.getFullName());
        assertEquals(FACULTY_NAME, result.getFacultyName());
        verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
    }

    @Test
    void getStudentById_WhenStudentNotFound_ShouldThrowException() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.getStudentById(STUDENT_ID)
        );

        assertEquals("Student not found by id: " + STUDENT_ID, exception.getMessage());
        verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
    }

    @Test
    void createStudent_WithValidData_ShouldCreateStudentSuccessfully() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        Student result = studentService.createStudent(validStudentDto);

        assertNotNull(result);
        verify(idGenerator, times(1)).generateUniqueId();
        verify(authenticationService, times(1)).createAuthentication(any(Student.class));
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createStudent_WithNullIdentityDocument_ShouldThrowValidationException() {
        StudentDto invalidDto = new StudentDto(null, "Juan Carlos Perez Gomez", PLAN, FACULTY_NAME, AcademicGrade.UNDERGRADUATE);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> studentService.createStudent(invalidDto)
        );

        assertEquals("Personal data is incomplete", exception.getMessage());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void createStudent_WithNullFullName_ShouldThrowValidationException() {
        StudentDto invalidDto = new StudentDto("12345678", null, PLAN, FACULTY_NAME, AcademicGrade.UNDERGRADUATE);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> studentService.createStudent(invalidDto)
        );

        assertEquals("Personal data is incomplete", exception.getMessage());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void createStudent_WithNullPlan_ShouldThrowValidationException() {
        StudentDto invalidDto = new StudentDto("12345678", "Juan Carlos Perez Gomez", null, FACULTY_NAME, AcademicGrade.UNDERGRADUATE);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> studentService.createStudent(invalidDto)
        );

        assertEquals("Academic Data is incomplete", exception.getMessage());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void createStudent_WithNullFacultyName_ShouldThrowValidationException() {
        StudentDto invalidDto = new StudentDto("12345678", "Juan Carlos Perez Gomez", PLAN, null, AcademicGrade.UNDERGRADUATE);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> studentService.createStudent(invalidDto)
        );

        assertEquals("Academic Data is incomplete", exception.getMessage());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void createStudent_WhenAuthenticationServiceThrowsException_ShouldThrowBusinessException() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        doThrow(new RuntimeException("Authentication service error"))
                .when(authenticationService).createAuthentication(any(Student.class));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> studentService.createStudent(validStudentDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when creating the student"));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void createStudent_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        doNothing().when(authenticationService).createAuthentication(any(Student.class));
        when(studentRepository.save(any(Student.class)))
                .thenThrow(new RuntimeException("Database error"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> studentService.createStudent(validStudentDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when creating the student"));
    }

    @Test
    void updateStudent_WhenStudentExists_ShouldUpdateAllFieldsSuccessfully() {
        StudentDto updateDto = new StudentDto("87654321", "Juan Carlos Perez Garcia", "New Plan", "Science", AcademicGrade.GRADUATED);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);

        Student result = studentService.updateStudent(STUDENT_ID, updateDto);

        assertNotNull(result);
        verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_WhenOnlyNameProvided_ShouldUpdateOnlyName() {
        StudentDto nameOnlyUpdateDto = new StudentDto(null, "Nuevo Nombre Completo", null, null, null);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);

        Student result = studentService.updateStudent(STUDENT_ID, nameOnlyUpdateDto);

        assertNotNull(result);
        assertEquals("Nuevo Nombre Completo", existingStudent.getFullName());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_WhenOnlyDocumentProvided_ShouldUpdateOnlyDocument() {
        StudentDto documentOnlyUpdateDto = new StudentDto("99999999", null, null, null, null);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);

        Student result = studentService.updateStudent(STUDENT_ID, documentOnlyUpdateDto);

        assertNotNull(result);
        assertEquals("99999999", existingStudent.getIdentityDocument());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_WhenOnlyFacultyProvided_ShouldUpdateOnlyFaculty() {
        StudentDto facultyOnlyUpdateDto = new StudentDto(null, null, null, "Arts", null);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);

        Student result = studentService.updateStudent(STUDENT_ID, facultyOnlyUpdateDto);

        assertNotNull(result);
        assertEquals("Arts", existingStudent.getFacultyName());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_WhenOnlyPlanProvided_ShouldUpdateOnlyPlan() {
        StudentDto planOnlyUpdateDto = new StudentDto(null, null, "New Plan", null, null);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);

        Student result = studentService.updateStudent(STUDENT_ID, planOnlyUpdateDto);

        assertNotNull(result);
        assertEquals("New Plan", existingStudent.getPlan());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_WhenOnlyAcademicGradeProvided_ShouldUpdateOnlyAcademicGrade() {
        StudentDto academicGradeOnlyUpdateDto = new StudentDto(null, null, null, null, AcademicGrade.GRADUATED);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);

        Student result = studentService.updateStudent(STUDENT_ID, academicGradeOnlyUpdateDto);

        assertNotNull(result);
        assertEquals(AcademicGrade.GRADUATED, existingStudent.getAcademicGrade());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_WithNoFieldsToUpdate_ShouldReturnSameStudent() {
        StudentDto emptyUpdateDto = new StudentDto(null, null, null, null, null);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenReturn(existingStudent);

        Student result = studentService.updateStudent(STUDENT_ID, emptyUpdateDto);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudent_WhenStudentNotFound_ShouldThrowException() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.updateStudent(STUDENT_ID, validStudentDto)
        );

        assertEquals("Student not found", exception.getMessage());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void updateStudent_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenThrow(new RuntimeException("Database error"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> studentService.updateStudent(STUDENT_ID, validStudentDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when updating the student"));
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void deleteStudent_WhenStudentExists_ShouldDeleteSuccessfully() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        doNothing().when(authenticationService).deleteAuthentication(existingStudent);
        doNothing().when(studentRepository).delete(existingStudent);

        Student result = studentService.deleteStudent(STUDENT_ID);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getUserId());
        verify(authenticationService, times(1)).deleteAuthentication(existingStudent);
        verify(studentRepository, times(1)).delete(existingStudent);
    }

    @Test
    void deleteStudent_WhenAuthenticationDeletionFails_ShouldThrowBusinessException() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        doThrow(new RuntimeException("Auth deletion failed"))
                .when(authenticationService).deleteAuthentication(existingStudent);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> studentService.deleteStudent(STUDENT_ID)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when deleting the student"));
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void deleteStudent_WhenStudentNotFound_ShouldThrowException() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.deleteStudent(STUDENT_ID)
        );

        assertEquals("Student not found", exception.getMessage());
        verify(authenticationService, never()).deleteAuthentication(any(Student.class));
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void deleteStudent_WhenRepositoryDeletionFails_ShouldThrowBusinessException() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        doNothing().when(authenticationService).deleteAuthentication(existingStudent);
        doThrow(new RuntimeException("Database error")).when(studentRepository).delete(existingStudent);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> studentService.deleteStudent(STUDENT_ID)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when deleting the student"));
        verify(authenticationService, times(1)).deleteAuthentication(existingStudent);
        verify(studentRepository, times(1)).delete(existingStudent);
    }

    @Test
    void getFacultyByStudentId_WhenStudentExists_ShouldReturnFacultyName() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));

        String result = studentService.getFacultyByStudentId(STUDENT_ID);

        assertEquals(FACULTY_NAME, result);
        verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
    }

    @Test
    void getFacultyByStudentId_WhenStudentNotFound_ShouldThrowException() {
        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.getFacultyByStudentId(STUDENT_ID)
        );

        assertEquals("Student not found", exception.getMessage());
        verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
    }


    @Test
    void createStudent_WithSpecialCharactersInName_ShouldGenerateValidEmail() {
        StudentDto studentWithSpecialChars = new StudentDto(
                "99999999",
                "José María Pérez",
                PLAN,
                FACULTY_NAME,
                AcademicGrade.UNDERGRADUATE
        );

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        Student result = studentService.createStudent(studentWithSpecialChars);

        assertNotNull(result);
        assertEquals("josé.maría-p@mail.escuelaing.edu.co", result.getEmail());
    }

    @Test
    void updateStudent_VerifyAllFieldsCanBeUpdatedSimultaneously() {
        StudentDto updateDto = new StudentDto("11111111", "New Full Name", "New Plan", "New Faculty", AcademicGrade.GRADUATED);

        when(studentRepository.findByUserId(STUDENT_ID))
                .thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Student result = studentService.updateStudent(STUDENT_ID, updateDto);

        assertNotNull(result);
        assertEquals("New Full Name", existingStudent.getFullName());
        assertEquals("11111111", existingStudent.getIdentityDocument());
        assertEquals("New Faculty", existingStudent.getFacultyName());
        assertEquals("New Plan", existingStudent.getPlan());
        assertEquals(AcademicGrade.GRADUATED, existingStudent.getAcademicGrade());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void createStudent_VerifyStudentBuilderUsage() {
        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> {
                    Student student = invocation.getArgument(0);
                    assertEquals(GENERATED_ID, student.getUserId());
                    assertEquals("Juan Carlos Perez Gomez", student.getFullName());
                    assertEquals("12345678", student.getIdentityDocument());
                    assertEquals(FACULTY_NAME, student.getFacultyName());
                    assertEquals(PLAN, student.getPlan());
                    assertEquals(AcademicGrade.UNDERGRADUATE, student.getAcademicGrade());
                    assertEquals(0, student.getGeneralAverage());
                    assertEquals("juan.perez-g@mail.escuelaing.edu.co", student.getEmail());
                    return student;
                });
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        Student result = studentService.createStudent(validStudentDto);

        assertNotNull(result);
        verify(studentRepository, times(1)).save(any(Student.class));
    }
    @Test
    void createStudent_WithNameContainingMultipleSpaces_ShouldGenerateCorrectEmail() {
        StudentDto studentWithExtraSpaces = new StudentDto(
                "55555555",
                "Luis Antonio Garcia Lopez",
                PLAN,
                FACULTY_NAME,
                AcademicGrade.UNDERGRADUATE
        );

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        Student result = studentService.createStudent(studentWithExtraSpaces);

        assertNotNull(result);
        assertEquals("luis.garcia-l@mail.escuelaing.edu.co", result.getEmail());
    }

    @Test
    void createStudent_WithAllAcademicGrades_ShouldCreateSuccessfully() {
        AcademicGrade[] grades = {AcademicGrade.UNDERGRADUATE, AcademicGrade.POSTGRADUATE,
                AcademicGrade.MASTERS_DEGREE, AcademicGrade.DOCTORS_DEGREE, AcademicGrade.GRADUATED};

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        for (AcademicGrade grade : grades) {
            StudentDto dto = new StudentDto("12345678", "Carlos Andres Lopez", PLAN, FACULTY_NAME, grade);

            Student result = studentService.createStudent(dto);
            assertNotNull(result);
            assertEquals(grade, result.getAcademicGrade());
        }
    }

    @Test
    void createStudent_WithNullAcademicGrade_ShouldCreateWithNullGrade() {
        StudentDto dtoWithNullGrade = new StudentDto("12345678", "Carlos Andres Lopez", PLAN, FACULTY_NAME, null);

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        Student result = studentService.createStudent(dtoWithNullGrade);

        assertNotNull(result);
        assertNull(result.getAcademicGrade());
    }

    @Test
    void createStudent_WithNameHavingExactlyThreeParts_ShouldGenerateCorrectEmail() {
        StudentDto studentWithThreeParts = new StudentDto(
                "55555555",
                "Ana Maria Lopez",
                PLAN,
                FACULTY_NAME,
                AcademicGrade.UNDERGRADUATE
        );

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        Student result = studentService.createStudent(studentWithThreeParts);

        assertNotNull(result);
        assertEquals("ana.maria-l@mail.escuelaing.edu.co", result.getEmail());
    }

    @Test
    void createStudent_WithNameHavingFourParts_ShouldGenerateCorrectEmail() {
        StudentDto studentWithFourParts = new StudentDto(
                "66666666",
                "Juan Carlos Perez Gomez",
                PLAN,
                FACULTY_NAME,
                AcademicGrade.UNDERGRADUATE
        );

        when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(authenticationService).createAuthentication(any(Student.class));

        Student result = studentService.createStudent(studentWithFourParts);

        assertNotNull(result);
        assertEquals("juan.perez-g@mail.escuelaing.edu.co", result.getEmail());
    }

    @Test
    void createStudent_WithInvalidNameForEmailGeneration_ShouldThrowValidationException() {
        StudentDto invalidNameDto = new StudentDto("12345678", "Nombre", PLAN, FACULTY_NAME, AcademicGrade.UNDERGRADUATE);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> studentService.createStudent(invalidNameDto)
        );

        assertTrue(exception.getMessage().contains("Invalid full name"));
        verify(studentRepository, never()).save(any(Student.class));
        verify(authenticationService, never()).createAuthentication(any(Student.class));
    }
}