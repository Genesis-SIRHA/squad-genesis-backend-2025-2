package edu.dosw.repositories;

import edu.dosw.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MembersRepository extends MongoRepository<Member, String> {

}
