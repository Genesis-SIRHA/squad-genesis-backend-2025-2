package edu.dosw.repositories;

import edu.dosw.model.Course;
import edu.dosw.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
    boolean existsByCode(String code);
    Group findByCode(String code);
}
