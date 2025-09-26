package edu.dosw.repositories;

import edu.dosw.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MembersRepository extends MongoRepository<User, String> {

}
