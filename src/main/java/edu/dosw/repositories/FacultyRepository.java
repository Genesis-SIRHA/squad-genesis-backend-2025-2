package edu.dosw.repositories;

import edu.dosw.model.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

/**
 * Repository interface for accessing and managing Course entities in MongoDB.
 * Extends MongoRepository to provide CRUD operations and custom query methods.
 */
public interface FacultyRepository extends MongoRepository<Faculty, String> {
    @Query("{ 'facultyName': ?0 , 'plan': ?1 }")
    Optional<Faculty> findByNameAndPlan(String facultyName, String plan);

}
