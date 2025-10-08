package edu.dosw.repositories;

import edu.dosw.model.Dean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface DeanRepository extends MongoRepository<Dean, String> {
    @Query("{ 'userId': ?0 }")
    public Optional<Dean> findByUserId(String userId);
}
