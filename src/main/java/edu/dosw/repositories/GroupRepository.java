package edu.dosw.repositories;

import edu.dosw.model.Group;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface GroupRepository extends MongoRepository<Group, String> {
  @Query("{ 'abbreviation': ?0 }")
  List<Group> findAllByCourseId(String abbreviation);

  Group findByGroupCode(String code);
}
