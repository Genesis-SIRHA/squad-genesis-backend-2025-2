package edu.dosw.services.UserServices;

import edu.dosw.dto.StudentDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Student;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.StudentRepository;
import edu.dosw.services.AuthenticationService;
import edu.dosw.utils.IdGenerator;
import jakarta.validation.ValidationException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class that provides business logic for managing Student entities. This service handles
 * operations such as creating, retrieving, updating, and deleting students. It also includes
 * functionality for generating student email addresses.
 */
@AllArgsConstructor
@Service
public class StudentService {
  private final StudentRepository studentRepository;
  private final IdGenerator idGenerator;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final AuthenticationService authenticationService;

  public Student getStudentById(String studentId) {
    Student student = studentRepository.findByUserId(studentId).orElse(null);
    if (student == null) {
      throw new ResourceNotFoundException("Student not found by id: " + studentId);
    }
    return student;
  }

  public Student createStudent(StudentDto studentCreationRequest) {
    if (studentCreationRequest.identityDocument() == null
        || studentCreationRequest.fullName() == null) {
      logger.error("Personal data is incomplete");
      throw new ValidationException("Personal data is incomplete");
    }

    if (studentCreationRequest.plan() == null || studentCreationRequest.facultyName() == null) {
      logger.error("Academic Data is incomplete");
      throw new ValidationException("Academic Data is incomplete");
    }

    String email = generateStudentEmail(studentCreationRequest.fullName());
    Student student =
        new Student.StudentBuilder()
            .userId(idGenerator.generateUniqueId())
            .fullName(studentCreationRequest.fullName())
            .email(email)
            .generalAverage(0)
            .academicGrade(studentCreationRequest.academicGrade())
            .identityDocument(studentCreationRequest.identityDocument())
            .plan(studentCreationRequest.plan())
            .semester(studentCreationRequest.semester())
            .facultyName(studentCreationRequest.facultyName())
            .build();
    try {
      authenticationService.createAuthentication(
          new UserInfoDto(student.getUserId(), student.getEmail(), Role.STUDENT, "pfpURL"));
      return studentRepository.save(student);
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when creating the student: " + e.getMessage());
    }
  }

  private String generateStudentEmail(String fullName) {
    String[] names = fullName.toLowerCase().split(" ");
    if (names.length < 3) {
      logger.error("Invalid full name");
      throw new ValidationException("Invalid full name: " + Arrays.toString(names));
    }
    String firstName = names[0];
    String lastName = names[names.length - 2];
    char secondLastName = names[names.length - 1].charAt(0);
    return firstName + "." + lastName + "-" + secondLastName + "@mail.escuelaing.edu.co";
  }

  public Student updateStudent(String studentId, StudentDto studentUpdateRequest) {
    Student student = studentRepository.findByUserId(studentId).orElse(null);
    if (student == null) {
      logger.error("Student not found");
      throw new ResourceNotFoundException("Student not found");
    }
    if (studentUpdateRequest.fullName() != null)
      student.setFullName(studentUpdateRequest.fullName());
    if (studentUpdateRequest.identityDocument() != null)
      student.setIdentityDocument(studentUpdateRequest.identityDocument());
    if (studentUpdateRequest.facultyName() != null)
      student.setFacultyName(studentUpdateRequest.facultyName());
    if (studentUpdateRequest.plan() != null) student.setPlan(studentUpdateRequest.plan());
    if (studentUpdateRequest.academicGrade() != null)
      student.setAcademicGrade(studentUpdateRequest.academicGrade());
    if (studentUpdateRequest.semester() != null)
      student.setSemester(studentUpdateRequest.semester());

    try {
      return studentRepository.save(student);
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when updating the student: " + e.getMessage());
    }
  }

  public Student deleteStudent(String studentId) {
    Student student = studentRepository.findByUserId(studentId).orElse(null);
    if (student == null) {
      logger.error("Student not found");
      throw new ResourceNotFoundException("Student not found");
    }
    try {
      authenticationService.deleteAuthentication(student);
      studentRepository.delete(student);
      return student;
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when deleting the student: " + e.getMessage());
    }
  }

  public String getFacultyByStudentId(String studentId) {
    Student student = studentRepository.findByUserId(studentId).orElse(null);
    if (student == null) {
      logger.error("Student not found");
      throw new ResourceNotFoundException("Student not found");
    }
    return student.getFacultyName();
  }
}
