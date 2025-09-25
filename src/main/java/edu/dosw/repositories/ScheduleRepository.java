package edu.dosw.repositories;

import edu.dosw.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Session, String> {

}
