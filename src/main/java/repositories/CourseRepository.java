package repositories;

import model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String> {
    boolean existsByCode(String code);
    Optional<Course> findByCode(String code);
}
