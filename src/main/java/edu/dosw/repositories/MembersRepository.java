package edu.dosw.repositories;

import edu.dosw.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MembersRepository extends MongoRepository<User, String> {
  Optional<User> findByUserId(String userId);
}
