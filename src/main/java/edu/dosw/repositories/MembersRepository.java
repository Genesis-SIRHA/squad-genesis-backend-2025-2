package edu.dosw.repositories;

import edu.dosw.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MembersRepository extends MongoRepository<User, String> {
    Optional<User> findByUserId(String userId);
}
