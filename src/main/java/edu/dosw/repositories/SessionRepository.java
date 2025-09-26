package edu.dosw.repositories;

import edu.dosw.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.ArrayList;

public interface SessionRepository extends MongoRepository<Session, String> {
    @Query("{  'groupCode': ?0 }")
    ArrayList<Session> findByGroupCode( String groupCode);
}
