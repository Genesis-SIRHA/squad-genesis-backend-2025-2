package edu.dosw.repositories;

import edu.dosw.model.Faculty;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Repository interface for accessing and managing Course entities in MongoDB. Extends
 * MongoRepository to provide CRUD operations and custom query methods.
 */
public interface FacultyRepository extends MongoRepository<Faculty, String> {
  @Query("{ 'facultyName': ?0 , 'plan': ?1 }")
  Optional<Faculty> findByNameAndPlan(String facultyName, String plan);

  @Query(value = "{'courses.abbreviation': ?0}", fields = "{'courses.$': 1}")
  Optional<Faculty> findFacultyByCourseAbbreviation(String abbreviation);
}
