package edu.dosw.repositories;

import edu.dosw.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String> {
    @Query("{ 'abbreviation': ?0 }")
    List<Group> findAllByCourseId(String abbreviation);
}
