package edu.dosw.repositories;

import edu.dosw.model.Professor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ProfessorRepository extends MongoRepository<Professor, String> {
    Optional<Professor> findById(String id);
    @Query("{ 'userId': ?0 }")
    Optional<Professor> findByUserId(String professorId);
}
