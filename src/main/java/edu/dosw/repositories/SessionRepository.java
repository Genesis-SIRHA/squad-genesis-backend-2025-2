package edu.dosw.repositories;

import edu.dosw.model.Session;
import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SessionRepository extends MongoRepository<Session, String> {
  @Query("{  'groupCode': ?0 }")
  ArrayList<Session> findByGroupCode(String groupCode);
}
