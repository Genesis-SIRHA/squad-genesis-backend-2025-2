package edu.dosw.repositories;

import edu.dosw.model.Student;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudentRepository extends MongoRepository<Student, String> {
  @Query("{ 'userId': ?0 }")
  Optional<Student> findByUserId(String studentId);
}
