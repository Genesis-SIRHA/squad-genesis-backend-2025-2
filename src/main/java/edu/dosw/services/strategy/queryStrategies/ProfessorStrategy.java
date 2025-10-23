package edu.dosw.services.strategy.queryStrategies;

import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.UserServices.ProfessorService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of QueryStrategy for professor users. This strategy allows professors to see all
 * exception requests that require administrative review or approval.
 */
@AllArgsConstructor
public class ProfessorStrategy implements QueryStrategy {
  private final RequestRepository requestRepository;
  private final ProfessorService professorService;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * Queries all exception requests that require administrative attention.
   *
   * @param userId The ID of the professor (not used in this implementation)
   * @return A list of exception requests requiring administrative review
   */
  @Override
  public List<Request> queryRequests(String userId) {
    String professorFaculty = professorService.getFacultyByProfessorId(userId);

    if (professorFaculty == null) {
      logger.error("User not found with id: " + userId);
      throw new ResourceNotFoundException("User not found with id: " + userId);
    }

    List<Request> allRequest = requestRepository.findAllAvailable();
    List<Request> allAvailable = new ArrayList<>();

    for (Request request : allRequest) {
      String studentId = request.getStudentId();
      String studentFaculty = professorService.getFacultyByProfessorId(studentId);
      if (professorFaculty.equals(studentFaculty)) {
        allAvailable.add(request);
      }
    }
    allAvailable.addAll(requestRepository.findOwnedBy(userId));
    return allAvailable;
  }
}
