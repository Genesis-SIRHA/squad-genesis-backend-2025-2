package edu.dosw.repositories;

import edu.dosw.model.Schedule;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    @DeleteQuery("{ 'studentId': ?0 }")
    void deleteByStudentId(String studentId);
}
