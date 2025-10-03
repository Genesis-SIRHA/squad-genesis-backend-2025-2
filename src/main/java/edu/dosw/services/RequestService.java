package edu.dosw.services;

import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import edu.dosw.repositories.FacultyRepository;
import edu.dosw.repositories.GroupRepository;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.strategy.AdministrativeStrategy;
import edu.dosw.services.strategy.AdministratorStrategy;
import edu.dosw.services.strategy.QueryStrategy;
import edu.dosw.services.strategy.StudentStrategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to requests. Manages request creation,
 * retrieval, status updates, and statistics. Uses a strategy pattern to handle different request
 * querying behaviors based on user roles.
 */
@Service
public class RequestService {

  private final RequestRepository requestRepository;
  private final FacultyRepository facultyRepository;
  private final GroupRepository groupRepository;
  private final Map<String, QueryStrategy> strategyMap;

  /**
   * Constructs a new RequestService with the required repositories. Initializes the strategy map
   * for different user roles.
   *
   * @param requestRepository The repository for request data access
   * @param facultyRepository The repository for course data access
   */
  @Autowired
  public RequestService(
      RequestRepository requestRepository,
      FacultyRepository facultyRepository,
      GroupRepository groupRepository) {
    this.requestRepository = requestRepository;
    this.facultyRepository = facultyRepository;
    this.strategyMap =
        Map.of(
            "STUDENT", new StudentStrategy(requestRepository),
            "ADMINISTRATIVE", new AdministrativeStrategy(requestRepository),
            "ADMINISTRATOR", new AdministratorStrategy(requestRepository));
    this.groupRepository = groupRepository;
  }

  /**
   * Fetches requests based on the user's role and ID. Uses the strategy pattern to determine which
   * requests are visible to the user.
   *
   * @param role The role of the user (STUDENT, ADMINISTRATIVE, or ADMINISTRATOR)
   * @param userId The ID of the user making the request
   * @return A list of requests visible to the user, sorted by creation date (newest first)
   * @throws IllegalArgumentException if the provided role is not supported
   */
  public List<Request> fetchRequests(String role, String userId) {
    QueryStrategy strategy = strategyMap.get(role.toUpperCase());

    if (strategy == null) {
      throw new IllegalArgumentException("Unsupported role: " + role);
    }
    return strategy.queryRequests(userId).stream()
        .sorted(Comparator.comparing(Request::getCreatedAt).reversed())
        .toList();
  }

  /**
   * Creates a new request with the provided details. Validates that both origin and destination
   * groups exist before creating the request.
   *
   * @param requestDTO The request data transfer object containing request details
   * @return A RequestResponse containing the created request
   * @throws IllegalArgumentException if either origin or destination group is not found
   */
  public Request createRequest(RequestDTO requestDTO) {
    Request request = new Request();
    request.setCreatedAt(LocalDateTime.now());
    request.setStatus("PENDING");
    request.setType(requestDTO.type());
    request.setStudentId(requestDTO.studentId());
    request.setExceptional(requestDTO.isExceptional());

    request.setAnswerAt(LocalDate.now());
    request.setGestedBy(requestDTO.studentId());
    request.setAnswer(requestDTO.description());
    request.setAnswerAt(LocalDate.now());

    Group origin = groupRepository.findByGroupCode(requestDTO.originGroupId());
    if (origin == null) {
      throw new IllegalArgumentException("Origin group not found: " + requestDTO.originGroupId());
    }

    Group destination = groupRepository.findByGroupCode(requestDTO.destinationGroupId());
    if (destination == null) {
      throw new IllegalArgumentException(
          "Destination group not found: " + requestDTO.destinationGroupId());
    }

    request.setOriginGroupId(origin.getGroupCode());
    request.setDestinationGroupId(destination.getGroupCode());

    return requestRepository.save(request);
  }

  /**
   * Updates the status of an existing request.
   *
   * @param id The ID of the request to update
   * @param status The new status to set for the request
   * @return The updated Request
   * @throws RuntimeException if no request is found with the given ID
   */
  public Request updateRequestStatus(String id, String status) {
    return requestRepository
        .findById(id)
        .map(
            request -> {
              request.setStatus(status);
              return requestRepository.save(request);
            })
        .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
  }

  /**
   * Retrieves statistics about requests, including total count and counts by status.
   *
   * @return A RequestStats object containing the statistics
   */
  public RequestStats getRequestStats() {
    long total = requestRepository.count();
    long pending = requestRepository.countByStatus("PENDING");
    long approved = requestRepository.countByStatus("APPROVED");
    long rejected = requestRepository.countByStatus("REJECTED");
    return new RequestStats(total, pending, approved, rejected);
  }

    /**
     *  Method that returns an answered request
     * @param requestId  : id of the request selected
     * @param response : the response of administration
     * @return  answered request
     */
    public Request respondToRequest(String requestId, Request response) {
        return requestRepository.findById(requestId)
                .map(existing -> {
                    existing.setAnswer(response.getAnswer());
                    existing.setGestedBy(response.getGestedBy());
                    existing.setAnswerAt(LocalDate.from(LocalDateTime.now()));

                    if (isValidStatus(response.getStatus())) {
                        existing.setStatus(response.getStatus());
                    } else {
                        throw new IllegalArgumentException("Invalid status. Must be APPROVED, REJECTED or PENDING");
                    }

                    return requestRepository.save(existing);
                })
                .orElse(null);
    }

    /**
     * method to validate status of requests
     * @param status : status of the request
     * @return boolean, is valid or invalid
     */
    private boolean isValidStatus(String status) {
        return "APPROVED".equalsIgnoreCase(status) ||
                "REJECTED".equalsIgnoreCase(status) ||
                "PENDING".equalsIgnoreCase(status);
    }

}
