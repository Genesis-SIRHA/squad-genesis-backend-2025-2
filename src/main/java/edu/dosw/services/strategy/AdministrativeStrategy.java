package edu.dosw.services.strategy;

import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.AdministrativeService;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of QueryStrategy for administrative users. This strategy allows administrative
 * users to see all available requests as well as any requests they have created themselves.
 */
public class AdministrativeStrategy implements QueryStrategy {

  private static final Logger logger = LoggerFactory.getLogger(AdministrativeStrategy.class);
  private final RequestRepository requestRepository;
  private final AdministrativeService administrativeService;

  /**
   * Constructs a new AdministrativeStrategy with the given request repository.
   *
   * @param requestRepository The repository used to access request data
   * @param administrativeService the service that manages persons in the university
   */
  @Autowired
  public AdministrativeStrategy(
      RequestRepository requestRepository, AdministrativeService administrativeService) {
    this.requestRepository = requestRepository;
    this.administrativeService = administrativeService;
  }

  /**
   * Queries all available requests and those created by the specified user.
   *
   * @param userId The ID of the administrative user
   * @return A combined list of all available requests and those created by the user
   */
  @Override
  public List<Request> queryRequests(String userId) {
    String professorFaculty = administrativeService.getFaculty(userId);

    if (professorFaculty == null) {
      logger.error("User not found with id: " + userId);
      throw new IllegalArgumentException("User not found with id: " + userId);
    }

    List<Request> allRequest = requestRepository.findAllAvailable();
    List<Request> allAvailable = new ArrayList<>();

    for (Request request : allRequest) {
      String studentId = request.getStudentId();
      String studentFaculty = administrativeService.getFaculty(studentId);
      if (professorFaculty.equals(studentFaculty)) {
        allAvailable.add(request);
      }
    }

    allAvailable.addAll(requestRepository.findOwnedBy(userId));
    return allAvailable;
  }
}
