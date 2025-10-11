package edu.dosw.repositories;

import edu.dosw.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<Group, String> {
  @Query("{ 'abbreviation': ?0 }")
  List<Group> findAllByCourseId(String abbreviation);

  @Query("{ 'groupCode': ?0 }")
  Optional<Group> findByGroupCode(String code);
}
