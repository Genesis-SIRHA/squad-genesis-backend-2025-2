package edu.dosw.services;

import edu.dosw.dto.CreateRequestPeriodDTO;
import edu.dosw.dto.RequestPeriodDTO;
import edu.dosw.dto.UpdateRequestPeriodDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.repositories.RequestPeriodRepository;
import edu.dosw.services.Validators.RequestPeriodValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RequestPeriodService {
  private static final Logger logger = LoggerFactory.getLogger(RequestPeriodService.class);
  private final RequestPeriodRepository requestPeriodRepository;
  private final PeriodService periodService;
  private final RequestPeriodValidator requestPeriodValidator;

  /**
   * Retrieves the currently active request period
   *
   * @return The active request period DTO
   * @throws ResourceNotFoundException If no active period is found
   */
  public RequestPeriodDTO getActivePeriod() {
    RequestPeriodDTO periodDTO = requestPeriodRepository.activePeriod();
    if (periodDTO == null) {
      logger.error("There is no active period of creation and answer of requests");
      throw new ResourceNotFoundException(
          "There is no active period of creation and answer of requests");
    }
    return periodDTO;
  }

  /**
   * Retrieves a specific request period by its unique identifier
   *
   * @param requestPeriodId The unique identifier of the request period
   * @return The request period DTO
   * @throws ResourceNotFoundException If no period is found with the given ID
   */
  public RequestPeriodDTO getPeriodById(String requestPeriodId) {
    RequestPeriodDTO periodDTO = requestPeriodRepository.getById(requestPeriodId);
    if (periodDTO == null) {
      logger.error("There is no Request period identified with the id: {}", requestPeriodId);
      throw new ResourceNotFoundException(
          "There is no Request period identified with the id: " + requestPeriodId);
    }
    return periodDTO;
  }

  /**
   * Retrieves all request periods in the system
   *
   * @return List of all request period DTOs
   */
  public List<RequestPeriodDTO> getAllPeriods() {
    return requestPeriodRepository.findAll();
  }

  /**
   * Creates a new active request period
   *
   * @param createRequestPeriodDTO The DTO containing request period creation data
   * @return The created request period DTO
   * @throws BusinessException If there is already an active period
   */
  public RequestPeriodDTO createActivePeriod(CreateRequestPeriodDTO createRequestPeriodDTO) {
    try {
      getActivePeriod();
    } catch (ResourceNotFoundException e) {
      RequestPeriodDTO periodDTO =
          new RequestPeriodDTO(
              UUID.randomUUID().toString(),
              createRequestPeriodDTO.initialDate(),
              createRequestPeriodDTO.finalDate(),
              createRequestPeriodDTO.year(),
              createRequestPeriodDTO.period(),
              true);
      requestPeriodValidator.createRequestPeriod(periodDTO);
      return requestPeriodRepository.save(periodDTO);
    }
    logger.error("There is still an active period of creation and answer of requests");
    throw new BusinessException(
        "There is still an active period of creation and answer of requests");
  }

  /**
   * Updates the currently active request period
   *
   * @param updateRequestPeriodDTO The DTO containing updated request period data
   * @return The updated request period DTO
   */
  public RequestPeriodDTO updateActivePeriod(UpdateRequestPeriodDTO updateRequestPeriodDTO) {
    RequestPeriodDTO periodDTO = requestPeriodRepository.activePeriod();
    LocalDate initialDate = periodDTO.initialDate();
    LocalDate finalDate = periodDTO.finalDate();
    boolean isActive = periodDTO.isActive();
    if (updateRequestPeriodDTO.initialDate() != null
        && !Objects.equals(periodDTO.initialDate(), updateRequestPeriodDTO.initialDate())) {
      initialDate = updateRequestPeriodDTO.initialDate();
    }
    if (updateRequestPeriodDTO.finalDate() != null
        && !Objects.equals(periodDTO.finalDate(), updateRequestPeriodDTO.finalDate())) {
      finalDate = updateRequestPeriodDTO.finalDate();
    }
    if (!updateRequestPeriodDTO.isActive()) {
      finalDate = LocalDate.now();
      isActive = false;
    }
    RequestPeriodDTO updatedPeriodDTO =
        new RequestPeriodDTO(
            periodDTO.id(), initialDate, finalDate, periodDTO.year(), periodDTO.period(), isActive);
    requestPeriodValidator.updateRequestPeriod(updatedPeriodDTO);

    return requestPeriodRepository.save(updatedPeriodDTO);
  }
}
