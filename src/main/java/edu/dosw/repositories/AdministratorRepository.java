package edu.dosw.repositories;

import edu.dosw.model.Administrator;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AdministratorRepository extends MongoRepository<Administrator, String> {

  Optional<Administrator> findById(String administratorId);

  @Query("{ 'userId': ?0 }")
  Optional<Administrator> findByUserId(String administratorId);
}
