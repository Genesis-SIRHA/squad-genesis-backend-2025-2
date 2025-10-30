package edu.dosw.services.UserServices;

import edu.dosw.dto.AdministratorDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceAlreadyExistsException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Administrator;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.AdministratorRepository;
import edu.dosw.services.AuthenticationService;
import edu.dosw.utils.IdGenerator;
import jakarta.validation.ValidationException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Service class that provides business logic for managing Administrator entities. This service
 * handles operations such as creating, retrieving, updating, and deleting administrators. It also
 * includes functionality for generating administrator email addresses.
 */
@AllArgsConstructor
@Service
public class AdministratorService {
  private final AdministratorRepository administratorRepository;
  private final IdGenerator idGenerator;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final AuthenticationService authenticationService;

  /**
   * Retrieves an administrator by their unique identifier.
   *
   * @param administratorId the unique identifier of the administrator to retrieve
   * @return the administrator with the specified ID, or null if not found
   */
  public Administrator getAdministratorById(String administratorId) {
    Administrator administrator =
        administratorRepository.findByUserId(administratorId).orElse(null);
    if (administrator == null) {
      throw new ResourceNotFoundException("Administrator not found by id: " + administratorId);
    }
    return administrator;
  }

  /**
   * Creates a new administrator with the provided information. Validates that all required personal
   * data is provided.
   *
   * @param administratorCreationRequest DTO containing the administrator's information
   * @return the newly created administrator
   * @throws BusinessException if required data is missing or an error occurs during creation
   */
  public Administrator createAdministrator(AdministratorDto administratorCreationRequest) {
    if (administratorCreationRequest.identityDocument() == null
        || administratorCreationRequest.fullName() == null) {
      logger.error("Personal data is incomplete");
      throw new ValidationException("Personal data is incomplete");
    }

    String email = generateAdministratorEmail(administratorCreationRequest.fullName());
    Administrator administrator =
        new Administrator.AdministratorBuilder()
            .userId(idGenerator.generateUniqueId())
            .fullName(administratorCreationRequest.fullName())
            .email(email)
            .identityDocument(administratorCreationRequest.identityDocument())
            .build();
    try {
      authenticationService.createAuthentication(
          new UserInfoDto(
              administrator.getUserId(), administrator.getEmail(), Role.ADMINISTRATOR, "pfpURL"));
      return administratorRepository.save(administrator);
    } catch (DataIntegrityViolationException e) {
      throw new ResourceAlreadyExistsException("Data integrity violation: " + e.getMessage());
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when creating the administrator");
    }
  }

  /**
   * Generates an email address for an administrator based on their full name. The email format is:
   * firstname.lastnameInitial@admin.escuelaing.edu.co
   *
   * @param fullName the full name of the administrator
   * @return the generated email address
   */
  private String generateAdministratorEmail(String fullName) {
    String[] names = fullName.toLowerCase().split(" ");
    if (names.length < 3) {
      logger.error("Invalid full name");
      throw new ValidationException("Invalid full name: " + Arrays.toString(names));
    }
    String firstName = names[0];
    String lastName = names[names.length - 2];
    char secondLastName = names[names.length - 1].charAt(0);
    return firstName + "." + lastName + "-" + secondLastName + "@admin.escuelaing.edu.co";
  }

  /**
   * Updates an existing administrator's information.
   *
   * @param administratorId the ID of the administrator to update
   * @param administratorUpdateRequest DTO containing the updated information
   * @return the updated administrator
   * @throws BusinessException if the administrator is not found or an error occurs during update
   */
  public Administrator updateAdministrator(
      String administratorId, AdministratorDto administratorUpdateRequest) {
    Administrator administrator =
        administratorRepository.findByUserId(administratorId).orElse(null);
    if (administrator == null) {
      logger.error("Administrator not found");
      throw new ResourceNotFoundException("Administrator not found");
    }
    if (administratorUpdateRequest.fullName() != null)
      administrator.setFullName(administratorUpdateRequest.fullName());
    if (administratorUpdateRequest.identityDocument() != null)
      administrator.setIdentityDocument(administratorUpdateRequest.identityDocument());

    try {
      return administratorRepository.save(administrator);
    } catch (DataIntegrityViolationException e) {
      throw new ResourceAlreadyExistsException(
          "Data integrity violation during update: " + e.getMessage());
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when updating the administrator");
    }
  }

  /**
   * Deletes an administrator by their ID.
   *
   * @param administratorId the ID of the administrator to delete
   * @return the deleted administrator
   * @throws BusinessException if the administrator is not found or an error occurs during deletion
   */
  public Administrator deleteAdministrator(String administratorId) {
    Administrator administrator =
        administratorRepository.findByUserId(administratorId).orElse(null);
    if (administrator == null) {
      logger.error("Administrator not found");
      throw new ResourceNotFoundException("Administrator not found");
    }
    try {
      authenticationService.deleteAuthentication(administrator);
      administratorRepository.delete(administrator);
      return administrator;
    } catch (Exception e) {
      throw new BusinessException(
          "An inesperated error has occurred when deleting the administrator");
    }
  }
}
