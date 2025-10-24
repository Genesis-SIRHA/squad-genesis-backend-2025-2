package edu.dosw.repositories;

import edu.dosw.model.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Repository interface for accessing and managing Request entities in MongoDB. Provides custom
 * query methods for retrieving requests based on various criteria.
 */
public interface RequestRepository extends MongoRepository<Request, String> {

  /**
   * Finds all requests with a status of 'PENDING'.
   *
   * @return A list of all pending requests
   */
  @Query("{ 'status': 'PENDING' }")
  List<Request> findAllAvailable();

  /**
   * Finds all requests managed by a specific professor.
   *
   * @param professorId The ID of the professor
   * @return A list of requests managed by the specified professor
   */
  @Query("{ 'managedBy': ?0 }")
  List<Request> findOwnedBy(String professorId);

  /**
   * Finds all requests submitted by a specific student.
   *
   * @param studentId The ID of the student
   * @return A list of requests submitted by the specified student
   */
  @Query("{ 'studentId': ?0 }")
  List<Request> findByStudentId(String studentId);

  /**
   * Counts the number of requests with a specific status.
   *
   * @param status The status to count (e.g., 'PENDING', 'ACCEPTED', 'REJECTED')
   * @return The count of requests with the specified status
   */
  long countByStatus(String status);

  @Query("{ 'faculty': ?0, 'isExceptional': true }")
  List<Request> findAvailableByFacultyAndIsExceptional();

  @Query("{ 'faculty': ?0 }")
  List<Request> findAvailableByFaculty(String faculty);

  @Query("{ 'requestId': ?0 }")
  Optional<Request> findByRequestId(String requestid);

    @Query(value = "{ 'destinationGroupId': ?0 }", sort = "{ 'createdAt': 1 }")
  List<Request> getRequestByDestinationGroupId(String destinationGroupCode);
}
