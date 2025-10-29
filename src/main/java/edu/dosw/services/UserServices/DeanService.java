package edu.dosw.services.UserServices;

import edu.dosw.dto.DeanDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Dean;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.DeanRepository;
import edu.dosw.services.AuthenticationService;
import edu.dosw.utils.IdGenerator;
import jakarta.validation.ValidationException;
import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class that provides business logic for managing Dean entities. This service handles
 * operations such as creating, retrieving, updating, and deleting deans, as well as retrieving
 * faculty information associated with deans.
 */
@Service
@AllArgsConstructor
public class DeanService {
  private final DeanRepository deanRepository;
  private final IdGenerator idGenerator;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final AuthenticationService authenticationService;

  /**
   * Retrieves a dean by their unique identifier.
   *
   * @param deanId the unique identifier of the dean to retrieve
   * @return the dean with the specified ID, or null if not found
   */
  public Dean getDeanById(String deanId) {
    Dean dean = deanRepository.findByUserId(deanId).orElse(null);
    if (dean == null) {
      throw new ResourceNotFoundException("Dean not found by id: " + deanId);
    }
    return dean;
  }

  /**
   * Creates a new dean with the provided information.
   *
   * @param deanCreationRequest DTO containing the dean's information
   * @return the newly created dean
   * @throws BusinessException if required personal data is missing or an error occurs during
   *     creation
   */
  public Dean createDean(DeanDto deanCreationRequest) {
    if (deanCreationRequest.identityDocument() == null || deanCreationRequest.fullName() == null) {
      logger.error("Personal data is incomplete");
      throw new ValidationException("Personal data is incomplete");
    }

    if (deanCreationRequest.facultyName() == null) {
      logger.error("Academic data is incomplete");
      throw new ValidationException("Academic data is incomplete");
    }

    String email = generateDeanEmail(deanCreationRequest.fullName());

    Dean dean =
        new Dean.DeanBuilder()
            .userId(idGenerator.generateUniqueId())
            .fullName(deanCreationRequest.fullName())
            .email(email)
            .identityDocument(deanCreationRequest.identityDocument())
            .facultyName(deanCreationRequest.facultyName())
            .build();
    try {
      authenticationService.createAuthentication(
          new UserInfoDto(dean.getUserId(), dean.getEmail(), Role.DEAN, "pfpURL"));
      return deanRepository.save(dean);
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when creating the dean: " + e.getMessage());
    }
  }

  /**
   * Generates an email address for a dean based on their full name. The email format is:
   * firstname.lastnameInitial@escuelaing.edu.co
   *
   * @param fullName the full name of the dean
   * @return the generated email address
   */
  private String generateDeanEmail(String fullName) {
    String[] names = fullName.toLowerCase().split(" ");
    if (names.length < 3) {
      logger.error("Invalid full name");
      throw new ValidationException("Invalid full name: " + Arrays.toString(names));
    }
    String firstName = names[0];
    String lastName = names[names.length - 1];
    char secondLastName = names[names.length - 2].charAt(0);
    return firstName + "." + lastName + "-" + secondLastName + "@escuelaing.edu.co";
  }

  /**
   * Updates an existing dean's information.
   *
   * @param deanId the ID of the dean to update
   * @param deanUpdateRequest DTO containing the updated information
   * @return the updated dean
   * @throws BusinessException if the dean is not found or an error occurs during update
   */
  public Dean updateDean(String deanId, DeanDto deanUpdateRequest) {
    Dean dean = deanRepository.findByUserId(deanId).orElse(null);
    if (dean == null) {
      logger.error("Dean not found");
      throw new ResourceNotFoundException("Dean not found");
    }

    if (deanUpdateRequest.fullName() != null) dean.setFullName(deanUpdateRequest.fullName());
    if (deanUpdateRequest.identityDocument() != null)
      dean.setIdentityDocument(deanUpdateRequest.identityDocument());
    if (deanUpdateRequest.facultyName() != null)
      dean.setFacultyName(deanUpdateRequest.facultyName());

    try {
      return deanRepository.save(dean);
    } catch (Exception e) {
      logger.error("Error updating dean: {}", e.getMessage(), e);
      throw new BusinessException(
          "An inesperated error has occurred when updating the dean: " + e.getMessage());
    }
  }

  /**
   * Deletes a dean by their ID.
   *
   * @param deanId the ID of the dean to delete
   * @return the deleted dean
   * @throws BusinessException if the dean is not found or an error occurs during deletion
   */
  public Dean deleteDean(String deanId) {
    Dean dean = deanRepository.findByUserId(deanId).orElse(null);
    if (dean == null) {
      logger.error("Dean not found");
      throw new ResourceNotFoundException("Dean not found");
    }
    try {
      authenticationService.deleteAuthentication(dean);
      deanRepository.delete(dean);
      return dean;
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when deleting the dean: " + e.getMessage());
    }
  }

  /**
   * Retrieves the faculty fullName associated with a user.
   *
   * @param deanId the unique identifier of the user
   * @return the faculty fullName associated with the user
   * @throws BusinessException if no user is found with the given ID
   */
  public String getFacultyByDeanId(String deanId) {
    Optional<Dean> user = deanRepository.findById(deanId);
    if (user.isEmpty()) {
      logger.error("User not found with deanId: " + deanId);
      throw new ResourceNotFoundException("User not found with deanId: " + deanId);
    }
    return user.get().getFacultyName();
  }
}
