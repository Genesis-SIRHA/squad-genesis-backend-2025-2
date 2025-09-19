package repositories;

import model.Course;
import model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
    boolean existsByCode(String code);
    Group findByCode(String code);
}
