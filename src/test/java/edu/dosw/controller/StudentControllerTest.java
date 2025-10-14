
package edu.dosw.controller;

import edu.dosw.dto.StudentDto;
import edu.dosw.model.Student;
import edu.dosw.model.enums.AcademicGrade;
import edu.dosw.services.UserServices.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private StudentDto studentDto;
    private Student student;
    private final String STUDENT_ID = "12345";

    @BeforeEach
    void setUp() {
        studentDto = new StudentDto(
                "ID123456",
                "Juan Pérez",
                "Engineering Plan",
                "Faculty of Engineering",
                AcademicGrade.UNDERGRADUATE
        );

        // Usando el Builder pattern de Student
        student = new Student.StudentBuilder()
                .userId(STUDENT_ID)
                .identityDocument("ID123456")
                .fullName("Juan Pérez")
                .email("juan.perez@university.edu")
                .plan("Engineering Plan")
                .facultyName("Faculty of Engineering")
                .generalAverage(85)
                .academicGrade(AcademicGrade.UNDERGRADUATE)
                .build();
    }

    @Test
    @DisplayName("GET /student/{id} - Should return student with all attributes when valid ID provided")
    void getStudentById_WhenValidId_ReturnsStudentWithAllAttributes() {
        // Arrange
        when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);

        // Act
        ResponseEntity<Student> response = studentController.getStudentById(STUDENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Student responseStudent = response.getBody();
        assertEquals(STUDENT_ID, responseStudent.getUserId());
        assertEquals("Juan Pérez", responseStudent.getFullName());
        assertEquals("ID123456", responseStudent.getIdentityDocument());
        assertEquals("juan.perez@university.edu", responseStudent.getEmail());
        assertEquals("Engineering Plan", responseStudent.getPlan());
        assertEquals("Faculty of Engineering", responseStudent.getFacultyName());
        assertEquals(85, responseStudent.getGeneralAverage());
        assertEquals(AcademicGrade.UNDERGRADUATE, responseStudent.getAcademicGrade());

        verify(studentService, times(1)).getStudentById(STUDENT_ID);
    }

    @Test
    @DisplayName("POST /student/create - Should create student with builder pattern")
    void createStudent_WhenValidRequest_ReturnsCreatedStudentUsingBuilder() {
        // Arrange
        Student newStudent = new Student.StudentBuilder()
                .userId("67890")
                .identityDocument("ID789012")
                .fullName("María García")
                .email("maria.garcia@university.edu")
                .plan("Computer Science Plan")
                .facultyName("Faculty of Computing")
                .generalAverage(90)
                .academicGrade(AcademicGrade.POSTGRADUATE)
                .build();

        when(studentService.createStudent(any(StudentDto.class))).thenReturn(newStudent);

        // Act
        ResponseEntity<Student> response = studentController.createStudent(studentDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("67890", response.getBody().getUserId());
        assertEquals("María García", response.getBody().getFullName());
        assertEquals(AcademicGrade.POSTGRADUATE, response.getBody().getAcademicGrade());
        verify(studentService, times(1)).createStudent(studentDto);
    }

    @Test
    @DisplayName("PATCH /student/update/{id} - Should update student with all academic grades")
    void updateStudent_WhenValidRequest_ReturnsUpdatedStudentWithAcademicGrade() {
        // Arrange
        StudentDto updateDto = new StudentDto(
                "ID123456",
                "Juan Pérez Updated",
                "Updated Engineering Plan",
                "Updated Faculty of Engineering",
                AcademicGrade.GRADUATED
        );

        Student updatedStudent = new Student.StudentBuilder()
                .userId(STUDENT_ID)
                .identityDocument("ID123456")
                .fullName("Juan Pérez Updated")
                .email("juan.perez.updated@university.edu")
                .plan("Updated Engineering Plan")
                .facultyName("Updated Faculty of Engineering")
                .generalAverage(88)
                .academicGrade(AcademicGrade.GRADUATED)
                .build();

        when(studentService.updateStudent(anyString(), any(StudentDto.class))).thenReturn(updatedStudent);

        // Act
        ResponseEntity<Student> response = studentController.updateStudent(updateDto, STUDENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Pérez Updated", response.getBody().getFullName());
        assertEquals("Updated Engineering Plan", response.getBody().getPlan());
        assertEquals(AcademicGrade.GRADUATED, response.getBody().getAcademicGrade());
        verify(studentService, times(1)).updateStudent(STUDENT_ID, updateDto);
    }

    @Test
    @DisplayName("DELETE /student/delete/{id} - Should delete student and return deleted entity")
    void deleteStudent_WhenValidId_ReturnsDeletedStudentWithAllData() {
        // Arrange
        when(studentService.deleteStudent(STUDENT_ID)).thenReturn(student);

        // Act
        ResponseEntity<Student> response = studentController.deleteStudent(STUDENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Student deletedStudent = response.getBody();
        assertEquals(STUDENT_ID, deletedStudent.getUserId());
        assertEquals("Juan Pérez", deletedStudent.getFullName());
        assertEquals("Engineering Plan", deletedStudent.getPlan());
        assertEquals(85, deletedStudent.getGeneralAverage());
        assertEquals(AcademicGrade.UNDERGRADUATE, deletedStudent.getAcademicGrade());

        verify(studentService, times(1)).deleteStudent(STUDENT_ID);
    }

    @Test
    @DisplayName("GET /student/{id} - Should handle student with masters degree")
    void getStudentById_WhenStudentHasMastersDegree_ReturnsStudent() {
        // Arrange
        Student mastersStudent = new Student.StudentBuilder()
                .userId("33333")
                .identityDocument("ID333333")
                .fullName("Dr. Carlos López")
                .email("carlos.lopez@university.edu")
                .plan("Research Plan")
                .facultyName("Faculty of Science")
                .generalAverage(95)
                .academicGrade(AcademicGrade.MASTERS_DEGREE)
                .build();

        when(studentService.getStudentById("33333")).thenReturn(mastersStudent);

        // Act
        ResponseEntity<Student> response = studentController.getStudentById("33333");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(AcademicGrade.MASTERS_DEGREE, response.getBody().getAcademicGrade());
        assertEquals(95, response.getBody().getGeneralAverage());
        verify(studentService, times(1)).getStudentById("33333");
    }

    @Test
    @DisplayName("POST /student/create - Should handle student with doctors degree")
    void createStudent_WhenStudentHasDoctorsDegree_ReturnsCreatedStudent() {
        // Arrange
        StudentDto doctorDto = new StudentDto(
                "ID555555",
                "Dr. Ana Martínez",
                "Doctoral Research Plan",
                "Faculty of Philosophy",
                AcademicGrade.DOCTORS_DEGREE
        );

        Student doctorStudent = new Student.StudentBuilder()
                .userId("55555")
                .identityDocument("ID555555")
                .fullName("Dr. Ana Martínez")
                .email("ana.martinez@university.edu")
                .plan("Doctoral Research Plan")
                .facultyName("Faculty of Philosophy")
                .generalAverage(98)
                .academicGrade(AcademicGrade.DOCTORS_DEGREE)
                .build();

        when(studentService.createStudent(doctorDto)).thenReturn(doctorStudent);

        // Act
        ResponseEntity<Student> response = studentController.createStudent(doctorDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(AcademicGrade.DOCTORS_DEGREE, response.getBody().getAcademicGrade());
        assertEquals(98, response.getBody().getGeneralAverage());
        verify(studentService, times(1)).createStudent(doctorDto);
    }

    @Test
    @DisplayName("GET /student/{id} - Should propagate service exception")
    void getStudentById_WhenServiceThrowsException_PropagatesException() {
        // Arrange
        String invalidId = "invalid-id";
        when(studentService.getStudentById(invalidId))
                .thenThrow(new RuntimeException("Student not found with id: " + invalidId));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentController.getStudentById(invalidId);
        });

        assertEquals("Student not found with id: invalid-id", exception.getMessage());
        verify(studentService, times(1)).getStudentById(invalidId);
    }

    @Test
    @DisplayName("PATCH /student/update/{id} - Should update student general average")
    void updateStudent_WhenUpdatingGeneralAverage_ReturnsUpdatedStudent() {
        // Arrange
        StudentDto updateDto = new StudentDto(
                "ID123456",
                "Juan Pérez",
                "Engineering Plan",
                "Faculty of Engineering",
                AcademicGrade.UNDERGRADUATE
        );

        Student updatedStudent = new Student.StudentBuilder()
                .userId(STUDENT_ID)
                .identityDocument("ID123456")
                .fullName("Juan Pérez")
                .email("juan.perez@university.edu")
                .plan("Engineering Plan")
                .facultyName("Faculty of Engineering")
                .generalAverage(92) // Updated average
                .academicGrade(AcademicGrade.UNDERGRADUATE)
                .build();

        when(studentService.updateStudent(STUDENT_ID, updateDto)).thenReturn(updatedStudent);

        // Act
        ResponseEntity<Student> response = studentController.updateStudent(updateDto, STUDENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(92, response.getBody().getGeneralAverage());
        verify(studentService, times(1)).updateStudent(STUDENT_ID, updateDto);
    }

    @Test
    @DisplayName("DELETE /student/delete/{id} - Should handle non-existent student")
    void deleteStudent_WhenStudentNotFound_PropagatesException() {
        // Arrange
        String nonExistentId = "99999";
        when(studentService.deleteStudent(nonExistentId))
                .thenThrow(new RuntimeException("Student not found with id: " + nonExistentId));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentController.deleteStudent(nonExistentId);
        });

        assertEquals("Student not found with id: 99999", exception.getMessage());
        verify(studentService, times(1)).deleteStudent(nonExistentId);
    }

    @Test
    @DisplayName("All operations - Should maintain data integrity across CRUD operations")
    void allOperations_ShouldMaintainStudentDataIntegrity() {
        // Arrange
        Student testStudent = new Student.StudentBuilder()
                .userId("77777")
                .identityDocument("ID777777")
                .fullName("Test Student")
                .email("test.student@university.edu")
                .plan("Test Plan")
                .facultyName("Test Faculty")
                .generalAverage(75)
                .academicGrade(AcademicGrade.UNDERGRADUATE)
                .build();

        when(studentService.getStudentById("77777")).thenReturn(testStudent);
        when(studentService.createStudent(any(StudentDto.class))).thenReturn(testStudent);
        when(studentService.updateStudent(anyString(), any(StudentDto.class))).thenReturn(testStudent);
        when(studentService.deleteStudent("77777")).thenReturn(testStudent);

        // Act & Assert - Test data integrity across all operations
        ResponseEntity<Student> getResponse = studentController.getStudentById("77777");
        assertStudentDataIntegrity(getResponse.getBody());

        ResponseEntity<Student> createResponse = studentController.createStudent(studentDto);
        assertStudentDataIntegrity(createResponse.getBody());

        ResponseEntity<Student> updateResponse = studentController.updateStudent(studentDto, "77777");
        assertStudentDataIntegrity(updateResponse.getBody());

        ResponseEntity<Student> deleteResponse = studentController.deleteStudent("77777");
        assertStudentDataIntegrity(deleteResponse.getBody());

        // Verify all service interactions
        verify(studentService, times(1)).getStudentById("77777");
        verify(studentService, times(1)).createStudent(studentDto);
        verify(studentService, times(1)).updateStudent("77777", studentDto);
        verify(studentService, times(1)).deleteStudent("77777");
    }

    private void assertStudentDataIntegrity(Student student) {
        assertNotNull(student);
        assertEquals("77777", student.getUserId());
        assertEquals("Test Student", student.getFullName());
        assertEquals("ID777777", student.getIdentityDocument());
        assertEquals("test.student@university.edu", student.getEmail());
        assertEquals("Test Plan", student.getPlan());
        assertEquals("Test Faculty", student.getFacultyName());
        assertEquals(75, student.getGeneralAverage());
        assertEquals(AcademicGrade.UNDERGRADUATE, student.getAcademicGrade());
    }
}