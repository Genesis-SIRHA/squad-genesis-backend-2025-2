package edu.dosw.services.strategy.queryStrategies;

import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.UserServices.DeanService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of QueryStrategy for dean users. This strategy allows dean users to see all
 * available requests as well as any requests they have created themselves.
 */
@AllArgsConstructor
public class DeanStrategy implements QueryStrategy {

  private static final Logger logger = LoggerFactory.getLogger(DeanStrategy.class);
  private final RequestRepository requestRepository;
  private final DeanService deanService;

  /**
   * Constructs a new DeanStrategy with the given request repository.
   *
   * @param requestRepository The repository used to access request data
   * @param deanService the service that manages persons in the university
   */

  /**
   * Queries all available requests and those created by the specified user.
   *
   * @param userId The ID of the dean user
   * @return A combined list of all available requests and those created by the user
   */
  @Override
  public List<Request> queryRequests(String userId) {
    String deanFaculty = deanService.getFacultyByDeanId(userId);
    if (deanFaculty == null) {
      logger.error("User not found with id: " + userId);
      throw new ResourceNotFoundException("User not found with id: " + userId);
    }

    return requestRepository.findAvailableByFacultyAndIsExceptional();
  }
}
