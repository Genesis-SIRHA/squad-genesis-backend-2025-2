package edu.dosw.repositories;

import edu.dosw.dto.UserCredentialsDto;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserCredentialsRepository extends MongoRepository<UserCredentialsDto, String> {
  @Query("{ 'email' : ?0 }")
  Optional<UserCredentialsDto> findByEmail(String email);

  @Query("{ 'userId' : ?0 }")
  Optional<UserCredentialsDto> findByUserId(String id);
}
