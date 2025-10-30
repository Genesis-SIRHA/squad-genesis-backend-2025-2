package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.StudentDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Student;
import edu.dosw.model.enums.AcademicGrade;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.StudentRepository;
import edu.dosw.services.UserServices.StudentService;
import edu.dosw.utils.IdGenerator;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock private StudentRepository studentRepository;

  @Mock private IdGenerator idGenerator;

  @Mock private AuthenticationService authenticationService;

  @InjectMocks private StudentService studentService;

  private StudentDto validStudentDto;
  private Student existingStudent;
  private final String STUDENT_ID = "student-123";
  private final String GENERATED_ID = "generated-student-456";
  private final String FACULTY_NAME = "Engineering";
  private final String PLAN = "Software Engineering";

  @BeforeEach
  void setUp() {
    validStudentDto =
        new StudentDto(
            "12345678",
            "Juan Carlos Perez Gomez",
            PLAN,
            FACULTY_NAME,
            AcademicGrade.UNDERGRADUATE,
            1);

    existingStudent =
        new Student.StudentBuilder()
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

    Exception exception =
        assertThrows(Exception.class, () -> method.invoke(studentService, "Nombre Apellido"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  @Test
  void generateStudentEmail_WithSingleWordName_ShouldThrowValidationException() throws Exception {
    Method method = StudentService.class.getDeclaredMethod("generateStudentEmail", String.class);
    method.setAccessible(true);

    Exception exception =
        assertThrows(Exception.class, () -> method.invoke(studentService, "Unico"));

    assertTrue(exception.getCause() instanceof jakarta.validation.ValidationException);
  }

  @Test
  void getStudentById_WhenStudentExists_ShouldReturnStudent() {
    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.of(existingStudent));

    Student result = studentService.getStudentById(STUDENT_ID);

    assertNotNull(result);
    assertEquals(STUDENT_ID, result.getUserId());
    assertEquals("Juan Carlos Perez Gomez", result.getFullName());
    assertEquals(FACULTY_NAME, result.getFacultyName());
    verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
  }

  @Test
  void getStudentById_WhenStudentNotFound_ShouldThrowException() {
    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> studentService.getStudentById(STUDENT_ID));

    assertEquals("Student not found by id: " + STUDENT_ID, exception.getMessage());
    verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
  }

  @Test
  void deleteStudent_WhenStudentExists_ShouldDeleteSuccessfully() {
    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.of(existingStudent));
    doNothing().when(authenticationService).deleteAuthentication(existingStudent);
    doNothing().when(studentRepository).delete(existingStudent);

    Student result = studentService.deleteStudent(STUDENT_ID);

    assertNotNull(result);
    assertEquals(STUDENT_ID, result.getUserId());
    verify(authenticationService, times(1)).deleteAuthentication(existingStudent);
    verify(studentRepository, times(1)).delete(existingStudent);
  }

  @Test
  void deleteStudent_WhenRepositoryDeletionFails_ShouldThrowBusinessException() {
    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.of(existingStudent));
    doNothing().when(authenticationService).deleteAuthentication(existingStudent);
    doThrow(new RuntimeException("Database error")).when(studentRepository).delete(existingStudent);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> studentService.deleteStudent(STUDENT_ID));

    assertTrue(
        exception
            .getMessage()
            .contains("An inesperated error has occurred when deleting the student"));
    verify(authenticationService, times(1)).deleteAuthentication(existingStudent);
    verify(studentRepository, times(1)).delete(existingStudent);
  }

  @Test
  void getFacultyByStudentId_WhenStudentExists_ShouldReturnFacultyName() {
    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.of(existingStudent));

    String result = studentService.getFacultyByStudentId(STUDENT_ID);

    assertEquals(FACULTY_NAME, result);
    verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
  }

  @Test
  void getFacultyByStudentId_WhenStudentNotFound_ShouldThrowException() {
    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> studentService.getFacultyByStudentId(STUDENT_ID));

    assertEquals("Student not found", exception.getMessage());
    verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
  }

  @Test
  void deleteStudent_WhenStudentNotFound_ShouldThrowResourceNotFoundException() {
    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> studentService.deleteStudent(STUDENT_ID));

    assertEquals("Student not found", exception.getMessage());
    verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
    verify(authenticationService, never()).deleteAuthentication(any(Student.class));
    verify(studentRepository, never()).delete(any(Student.class));
  }

  @Test
  void updateStudent_WithAllFields_ShouldUpdateAllFieldsSuccessfully() {

    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.of(existingStudent));
    when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);

    StudentDto updateRequest =
        new StudentDto(
            "87654321",
            "Juan Carlos Perez Rodriguez",
            "Updated Software Engineering",
            "Computer Science",
            AcademicGrade.GRADUATED,
            5);

    // Act
    Student result = studentService.updateStudent(STUDENT_ID, updateRequest);

    assertNotNull(result);
    assertEquals("Juan Carlos Perez Rodriguez", existingStudent.getFullName());
    assertEquals("87654321", existingStudent.getIdentityDocument());
    assertEquals("Computer Science", existingStudent.getFacultyName());
    assertEquals("Updated Software Engineering", existingStudent.getPlan());
    assertEquals(AcademicGrade.GRADUATED, existingStudent.getAcademicGrade());
    assertEquals(Integer.valueOf(5), existingStudent.getSemester());
    verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
    verify(studentRepository, times(1)).save(existingStudent);
  }

  @Test
  void updateStudent_WhenStudentNotFound_ShouldThrowResourceNotFoundException() {

    when(studentRepository.findByUserId(STUDENT_ID)).thenReturn(Optional.empty());

    StudentDto updateRequest =
        new StudentDto(
            "87654321", "Nombre Actualizado", PLAN, FACULTY_NAME, AcademicGrade.UNDERGRADUATE, 2);

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> studentService.updateStudent(STUDENT_ID, updateRequest));

    assertEquals("Student not found", exception.getMessage());
    verify(studentRepository, times(1)).findByUserId(STUDENT_ID);
    verify(studentRepository, never()).save(any(Student.class));
  }

  @Test
  void createStudent_WithValidData_ShouldCreateStudentSuccessfully() {

    when(idGenerator.generateUniqueId()).thenReturn(GENERATED_ID);
    doNothing().when(authenticationService).createAuthentication(any(UserInfoDto.class));

    Student expectedStudent =
        new Student.StudentBuilder()
            .userId(GENERATED_ID)
            .fullName(validStudentDto.fullName())
            .email("juan.perez-g@mail.escuelaing.edu.co")
            .generalAverage(0)
            .academicGrade(validStudentDto.academicGrade())
            .identityDocument(validStudentDto.identityDocument())
            .plan(validStudentDto.plan())
            .semester(validStudentDto.semester())
            .facultyName(validStudentDto.facultyName())
            .build();

    when(studentRepository.save(any(Student.class))).thenReturn(expectedStudent);

    Student result = studentService.createStudent(validStudentDto);

    assertNotNull(result);
    assertEquals(GENERATED_ID, result.getUserId());
    assertEquals(validStudentDto.fullName(), result.getFullName());
    assertEquals("juan.perez-g@mail.escuelaing.edu.co", result.getEmail());
    assertEquals(validStudentDto.identityDocument(), result.getIdentityDocument());
    assertEquals(validStudentDto.plan(), result.getPlan());
    assertEquals(validStudentDto.facultyName(), result.getFacultyName());
    assertEquals(validStudentDto.academicGrade(), result.getAcademicGrade());
    assertEquals(validStudentDto.semester(), result.getSemester());
    assertEquals(0, result.getGeneralAverage());

    verify(idGenerator, times(1)).generateUniqueId();
    verify(authenticationService, times(1)).createAuthentication(any(UserInfoDto.class));
    verify(studentRepository, times(1)).save(any(Student.class));

    ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
    verify(authenticationService).createAuthentication(userInfoCaptor.capture());

    UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
    assertEquals(GENERATED_ID, capturedUserInfo.userId());
    assertEquals("juan.perez-g@mail.escuelaing.edu.co", capturedUserInfo.email());
    assertEquals(Role.STUDENT, capturedUserInfo.role());
    assertEquals("pfpURL", capturedUserInfo.pfpURL());
  }
}
