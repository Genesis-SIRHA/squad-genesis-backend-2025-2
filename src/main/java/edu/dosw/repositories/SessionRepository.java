package edu.dosw.repositories;

import edu.dosw.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.ArrayList;

public interface SessionRepository extends MongoRepository<Session, String> {
    @Query("{ 'abbreviation': ?0, 'groupCode': ?1 }")
    ArrayList<Session> findByAbbreviationAndGroupCode(String abbreviation, String groupCode);
}
