package edu.dosw.services.strategy.queryStrategies;

import edu.dosw.model.Request;
import java.util.List;

/**
 * Interface that defines the contract for different strategies to query requests based on user
 * roles. Implementations of this interface provide specific querying logic for different types of
 * users in the system.
 */
public interface QueryStrategy {

  /**
   * Queries requests based on the specific strategy implementation.
   *
   * @param userId The ID of the user making the request
   * @return A list of requests that the user is authorized to see
   */
  List<Request> queryRequests(String userId);
}
