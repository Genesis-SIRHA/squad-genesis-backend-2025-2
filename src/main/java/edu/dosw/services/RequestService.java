package edu.dosw.services;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.RequestStats;
import edu.dosw.dto.UpdateRequestDto;
import edu.dosw.model.Request;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.strategy.DeanStrategy;
import edu.dosw.services.strategy.ProfessorStrategy;
import edu.dosw.services.strategy.QueryStrategy;
import edu.dosw.services.strategy.StudentStrategy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to requests. Manages request creation,
 * retrieval, status updates, and statistics. Uses a strategy pattern to handle different request
 * querying behaviors based on user roles.
 */
@Service
public class RequestService {

  private static final Logger logger = LoggerFactory.getLogger(RequestService.class);
  private final RequestRepository requestRepository;
  private final ValidatorService validatorService;
  private final AuthenticationService authenticationService;
  private final StudentService studentService;
  private final Map<Role, QueryStrategy> strategyMap;

  @Autowired
  public RequestService(
      RequestRepository requestRepository,
      ValidatorService validatorService,
      DeanService deanService,
      ProfessorService professorService,
      StudentService studentService,
      AuthenticationService authenticationService
      ) {
    this.requestRepository = requestRepository;
    this.validatorService = validatorService;
    this.authenticationService = authenticationService;
    this.studentService = studentService;
    this.strategyMap =
        Map.of(
            Role.STUDENT, new StudentStrategy(requestRepository, studentService),
            Role.DEAN, new DeanStrategy(requestRepository, deanService),
            Role.PROFESSOR, new ProfessorStrategy(requestRepository, professorService));
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
  public List<Request> fetchRequests(Role role, String userId) {
      logger.info("Fetching requests for user: {} with role: {}", userId, role);

      QueryStrategy strategy = strategyMap.get(role);

      if (strategy == null) {
        logger.error("Unsupported role: {}", role);
        throw new IllegalArgumentException("Unsupported role: " + role);
      }
      return strategy.queryRequests(userId).stream()
        .sorted(Comparator.comparing(Request::getCreatedAt))
        .toList();
  }

  public List<Request> fetchAllRequests() {
    try {
      return requestRepository.findAll().stream()
        .sorted(Comparator.comparing(Request::getCreatedAt))
        .toList();
    }catch (Exception e){
      logger.error("Failed to fetch all requests: {}", e.getMessage());
      throw new RuntimeException("Failed to fetch all requests: " + e.getMessage());
    }
  }

  /**
   * Creates a new request with the provided details. Validates that both origin and destination
   * groups exist before creating the request.
   *
   * @param requestDTO The request data transfer object containing request details
   * @return A RequestResponse containing the created request
   * @throws IllegalArgumentException if either origin or destination group is not found
   */
  public Request createRequest(CreateRequestDto requestDTO) {
      validatorService.validateRequest(requestDTO);

      Request request = new Request.RequestBuilder()
              .studentId(requestDTO.studentId())
              .type(requestDTO.type())
              .description(requestDTO.description())
              .destinationGroupId(requestDTO.destinationGroupId())
              .originGroupId(requestDTO.originGroupId())
              .build();
      try{
        return requestRepository.save(request);
      }catch(Exception e){
          logger.error("Failed to create request: {}", e.getMessage());
        throw new RuntimeException("Failed to create request: " + e.getMessage());
      }
  }

  /**
   * Updates the status of an existing request.
   *
   * @param userId The ID of the request to update
   * @param updateRequestDto The update request dto containing the new state
   * @return The updated Request
   * @throws RuntimeException if no request is found with the given ID
   */
  public Request updateRequest(String userId, UpdateRequestDto updateRequestDto) {
    Request request = requestRepository.findByRequestId(updateRequestDto.requestId()).orElse(null);
    validatorService.validateUpdateRequest(userId, request, updateRequestDto);

    request.setStatus(updateRequestDto.status());
    if (updateRequestDto.answer() != null) request.setAnswer(updateRequestDto.answer());
    if (updateRequestDto.managedBy() != null) request.setGestedBy(updateRequestDto.managedBy());
    request.setUpdatedAt(LocalDate.now());

    try{
        return requestRepository.save(request);
    } catch (Exception e){
        logger.error("Failed to update request status: {}", e.getMessage());
        throw new RuntimeException("Failed to update request status: " + e.getMessage());
    }
  }

  /**
   * Retrieves statistics about requests, including total count and counts by status.
   *
   * @return A RequestStats object containing the statistics
   */
  public RequestStats getRequestStats() {
    long total = requestRepository.count();
    long pending = requestRepository.countByStatus("PENDING");
    long approved = requestRepository.countByStatus("ACCEPTED");
    long rejected = requestRepository.countByStatus("REJECTED");
    return new RequestStats(total, pending, approved, rejected);
  }

    public Request deleteRequestStatus(String requestId) {
        Request request = requestRepository.findByRequestId(requestId).orElse(null);
        if (request == null) {
            throw new RuntimeException("Request not found with id: " + requestId);
        }

        try{
            requestRepository.delete(request);
            return request;
        }catch(Exception e){
            logger.error("Failed to delete request: {}", e.getMessage());
            throw new RuntimeException("Failed to delete request: " + e.getMessage());
        }
    }

    public Request getRequest(String requestId) {
      Request request = requestRepository.findByRequestId(requestId).orElse(null);
      if (request == null) {
          logger.error("Request not found with id: {}", requestId);
        throw new RuntimeException("Request not found with id: " + requestId);
      }
      return request;
    }

    public List<Request> fetchRequestsByFacultyName(String facultyName) {
      validatorService.validateFacultyName(facultyName);
      List<Request> requests = requestRepository.findAll();
      List<Request> facultyRequest = new ArrayList<>();

      for (Request request : requests) {
          String requestFaculty = studentService.getFacultyByStudentId(request.getStudentId());
        if (requestFaculty.equals(facultyName)) {
          facultyRequest.add(request);
        }
      }
      try{
          return facultyRequest.stream()
            .sorted(Comparator.comparing(Request::getCreatedAt).reversed())
            .toList();
      }catch (Exception e){
          logger.error("Failed to fetch requests by faculty name: {}", e.getMessage());
          throw new RuntimeException("Failed to fetch requests by faculty name: " + e.getMessage());
      }
    }
}
