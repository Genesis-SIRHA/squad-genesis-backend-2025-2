package edu.dosw.repositories;

import edu.dosw.model.Administrative;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdministrativeRepository extends MongoRepository<Administrative, String> {
    public Optional<Administrative> findById(String id);
}
