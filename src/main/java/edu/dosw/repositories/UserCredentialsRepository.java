package edu.dosw.repositories;

import edu.dosw.dto.UserCredentialsDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserCredentialsRepository extends MongoRepository<UserCredentialsDto, String> {
    Optional<UserCredentialsDto> findByEmail(String email);
    @Query("{ 'userId' : ?0 }")
    Optional<UserCredentialsDto> findByUserId(String id);
}
