package edu.dosw.services;

import edu.dosw.dto.StudentDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Student;
import edu.dosw.repositories.StudentRepository;
import edu.dosw.utils.IdGenerator;
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

  /**
   * Retrieves a student by their unique identifier.
   *
   * @param studentId the unique identifier of the student to retrieve
   * @return the student with the specified ID, or null if not found
   */
  public Student getStudentById(String studentId) {
    Student student = studentRepository.findByUserId(studentId).orElse(null);
    if (student == null) {
      throw new BusinessException("Dean not found by id: " + studentId);
    }
    return student;
  }

  /**
   * Creates a new student with the provided information. Validates that all required personal and
   * academic data is provided.
   *
   * @param studentCreationRequest DTO containing the student's information
   * @return the newly created student
   * @throws BusinessException if required data is missing or an error occurs during creation
   */
  public Student createStudent(StudentDto studentCreationRequest) {
    if (studentCreationRequest.identityDocument() == null
        || studentCreationRequest.fullName() == null) {
      logger.error("Personal data is incomplete");
      throw new BusinessException("Personal data is incomplete");
    }

    if (studentCreationRequest.plan() == null || studentCreationRequest.facultyName() == null) {
      logger.error("Academic Data is incomplete");
      throw new BusinessException("Academic Data is incomplete");
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
            .facultyName(studentCreationRequest.facultyName())
            .build();
    try {
      authenticationService.createAuthentication(student);
      return studentRepository.save(student);
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when creating the student: " + e.getMessage());
    }
  }

  /**
   * Generates an email address for a student based on their full name. The email format is:
   * firstname.lastnameInitial@mail.escuelaing.edu.co
   *
   * @param fullName the full name of the student
   * @return the generated email address
   */
  private String generateStudentEmail(String fullName) {
    String[] names = fullName.toLowerCase().split(" ");
    if (names.length < 3) {
      logger.error("Invalid full name");
      throw new BusinessException("Invalid full name: " + Arrays.toString(names));
    }
    String firstName = names[0];
    String lastName = names[names.length - 2];
    char secondLastName = names[names.length - 1].charAt(0);
    return firstName + "." + lastName + "-" + secondLastName + "@mail.escuelaing.edu.co";
  }

  /**
   * Updates an existing student's information.
   *
   * @param studentId the ID of the student to update
   * @param studentUpdateRequest DTO containing the updated information
   * @return the updated student
   * @throws BusinessException if the student is not found or an error occurs during update
   */
  public Student updateStudent(String studentId, StudentDto studentUpdateRequest) {
    Student student = studentRepository.findByUserId(studentId).orElse(null);
    if (student == null) {
      logger.error("Student not found");
      throw new BusinessException("Student not found");
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

    try {
      return studentRepository.save(student);
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when updating the student: " + e.getMessage());
    }
  }

  /**
   * Deletes a student by their ID.
   *
   * @param studentId the ID of the student to delete
   * @return the deleted student
   * @throws BusinessException if the student is not found or an error occurs during deletion
   */
  public Student deleteStudent(String studentId) {
    Student student = studentRepository.findByUserId(studentId).orElse(null);
    if (student == null) {
      logger.error("Student not found");
      throw new BusinessException("Student not found");
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
}
