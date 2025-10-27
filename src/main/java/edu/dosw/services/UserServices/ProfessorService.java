package edu.dosw.services.UserServices;

import edu.dosw.dto.ProfessorDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Professor;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.ProfessorRepository;
import edu.dosw.services.AuthenticationService;
import edu.dosw.utils.IdGenerator;
import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProfessorService {
  private final ProfessorRepository professorRepository;
  private final IdGenerator idGenerator;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final AuthenticationService authenticationService;

  /**
   * Retrieves a professor by their unique identifier.
   *
   * @param professorId the unique identifier of the professor to retrieve
   * @return the professor with the specified ID, or null if not found
   */
  public Professor getProfessorById(String professorId) {
    Professor professor = professorRepository.findByUserId(professorId).orElse(null);
    if (professor == null) {
      throw new BusinessException("Professor not found by id: " + professorId);
    }
    return professor;
  }

  /**
   * Creates a new professor with the provided information.
   *
   * @param professorCreationRequest DTO containing the professor's information
   * @return the newly created professor
   * @throws BusinessException if required personal data is missing or an error occurs during
   *     creation
   */
  public Professor createProfessor(ProfessorDto professorCreationRequest) {
    if (professorCreationRequest.identityDocument() == null
        || professorCreationRequest.fullName() == null) {
      logger.error("Personal data is incomplete");
      throw new BusinessException("Personal data is incomplete");
    }

    if (professorCreationRequest.facultyName() == null) {
      logger.error("Academic data is incomplete");
      throw new BusinessException("Academic data is incomplete");
    }

    String email = generateProfessorEmail(professorCreationRequest.fullName());

    Professor professor =
        new Professor.ProfessorBuilder()
            .userId(idGenerator.generateUniqueId())
            .fullName(professorCreationRequest.fullName())
            .email(email)
            .identityDocument(professorCreationRequest.identityDocument())
            .facultyName(professorCreationRequest.facultyName())
            .build();
    try {
      authenticationService.createAuthentication(
          new UserInfoDto(professor.getUserId(), professor.getEmail(), Role.PROFESSOR));
      return professorRepository.save(professor);
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when creating the professor: " + e.getMessage());
    }
  }

  /**
   * Generates an email address for a professor based on their full name. The email format is:
   * firstname.lastnameInitial@escuelaing.edu.co
   *
   * @param fullName the full name of the professor
   * @return the generated email address
   */
  private String generateProfessorEmail(String fullName) {
    String[] names = fullName.toLowerCase().split(" ");
    if (names.length < 3) {
      logger.error("Invalid full name");
      throw new BusinessException("Invalid full name: " + Arrays.toString(names));
    }
    String firstName = names[0];
    String lastName = names[names.length - 2];
    char secondLastName = names[names.length - 1].charAt(0);
    return firstName + "." + lastName + "-" + secondLastName + "@escuelaing.edu.co";
  }

  /**
   * Updates an existing professor's information.
   *
   * @param professorId the ID of the professor to update
   * @param professorUpdateRequest DTO containing the updated information
   * @return the updated professor
   * @throws BusinessException if the professor is not found or an error occurs during update
   */
  public Professor updateProfessor(String professorId, ProfessorDto professorUpdateRequest) {
    Professor professor = professorRepository.findByUserId(professorId).orElse(null);
    if (professor == null) {
      logger.error("Professor not found");
      throw new BusinessException("Professor not found");
    }
    if (professorUpdateRequest.fullName() != null)
      professor.setFullName(professorUpdateRequest.fullName());
    if (professorUpdateRequest.identityDocument() != null)
      professor.setIdentityDocument(professorUpdateRequest.identityDocument());
    if (professorUpdateRequest.facultyName() != null)
      professor.setFacultyName(professorUpdateRequest.facultyName());

    try {
      return professorRepository.save(professor);
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when updating the professor: " + e.getMessage());
    }
  }

  /**
   * Deletes a professor by their ID.
   *
   * @param professorId the ID of the professor to delete
   * @return the deleted professor
   * @throws BusinessException if the professor is not found or an error occurs during deletion
   */
  public Professor deleteProfessor(String professorId) {
    Professor professor = professorRepository.findByUserId(professorId).orElse(null);
    if (professor == null) {
      logger.error("Professor not found");
      throw new BusinessException("Professor not found");
    }
    try {
      authenticationService.deleteAuthentication(professor);
      professorRepository.delete(professor);
      return professor;
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when deleting the professor:" + e.getMessage());
    }
  }

  /**
   * Retrieves the faculty fullName associated with a user.
   *
   * @param professorId the unique identifier of the user
   * @return the faculty fullName associated with the user
   * @throws BusinessException if no user is found with the given ID
   */
  public String getFacultyByProfessorId(String professorId) {
    Optional<Professor> user = professorRepository.findById(professorId);
    if (user.isEmpty()) {
      logger.error("User not found with professorId: " + professorId);
      throw new BusinessException("User not found with professorId: " + professorId);
    }
    return user.get().getFacultyName();
  }
}
