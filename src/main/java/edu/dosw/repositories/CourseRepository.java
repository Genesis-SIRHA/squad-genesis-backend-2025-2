package edu.dosw.repositories;

import edu.dosw.model.Course;
import edu.dosw.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for accessing and managing Course entities in MongoDB.
 * Extends MongoRepository to provide CRUD operations and custom query methods.
 */
public interface CourseRepository extends MongoRepository<Course, String> {
    
    /**
     * Checks if a course with the given code exists in the database.
     * 
     * @param code The course code to check
     * @return true if a course with the given code exists, false otherwise
     */
    boolean existsByCode(String code);
    
    /**
     * Finds a group by its code.
     * 
     * @param code The group code to search for
     * @return The Group entity if found, or null if no group with the given code exists
     */
    Group findByCode(String code);
}
