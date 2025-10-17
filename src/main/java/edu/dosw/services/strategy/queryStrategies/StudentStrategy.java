package edu.dosw.services.strategy.queryStrategies;

import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Request;
import edu.dosw.model.Student;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.UserServices.StudentService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of QueryStrategy for student users. This strategy allows students to see only
 * their own requests.
 */
public class StudentStrategy implements QueryStrategy {
  private final RequestRepository requestRepository;
  private final StudentService studentService;
  private static final Logger logger = LoggerFactory.getLogger(StudentStrategy.class);

  /**
   * Constructs a new StudentStrategy with the given request repository.
   *
   * @param requestRepository The repository used to access request data
   */
  public StudentStrategy(RequestRepository requestRepository, StudentService studentService) {
    this.requestRepository = requestRepository;
    this.studentService = studentService;
  }

  /**
   * Queries requests that belong to the specified student.
   *
   * @param userId The ID of the student whose requests to retrieve
   * @return A list of requests created by the specified student
   */
  @Override
  public List<Request> queryRequests(String userId) {
    Student student = studentService.getStudentById(userId);
    if (student == null) {
      logger.error("Student not found with id: {}", userId);
      throw new ResourceNotFoundException("Student not found with id: " + userId);
    }
    return requestRepository.findByStudentId(userId);
  }
}
