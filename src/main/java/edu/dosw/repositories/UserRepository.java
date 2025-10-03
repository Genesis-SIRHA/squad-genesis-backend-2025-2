package edu.dosw.repositories;

import edu.dosw.dto.UserCredentialsDto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserCredentialsDto, String> {
    Optional<UserCredentialsDto> findByEmail(String email);
}
