package edu.dosw.repositories;

import edu.dosw.model.Request;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RequestRepository extends MongoRepository<Request, String> {
    @Query("{ 'status': 'PENDING' }")
    List<Request> findAllAvailable();

    @Query("{ 'managedBy': ?0 , 'faculty': ?1 }")
    List<Request> findOwnedBy(String professorId, String faculty);

    @Query("{ 'status': 'EXCEPTIONAL' }")
    List<Request> queryExceptionRequest();

    @Query("{ 'studentId': ?0 }")
    List<Request> findByStudentId(String studentId);

    long countByStatus(String status);

}
