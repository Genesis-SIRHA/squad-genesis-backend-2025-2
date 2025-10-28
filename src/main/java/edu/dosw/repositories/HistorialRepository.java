package edu.dosw.repositories;

import edu.dosw.model.Historial;
import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface HistorialRepository extends MongoRepository<Historial, String> {

  @Query("{ 'studentId': ?0, 'year': ?1, 'period': ?2 , 'status': 'ON_GOING'}")
  ArrayList<Historial> findCurrentHistorialByStudentIdAndYearAndPeriod(
      String studentId, String year, String period);

  @Query(
      "{ 'studentId': ?0, 'year': ?1, 'period': ?2, 'status': { $in: ['ON_GOING', 'FINISHED'] } }")
  ArrayList<Historial> findHistorialByStudentIdAndYearAndPeriod(
      String studentId, String year, String period);

  @Query("{ 'studentId': ?0}")
  ArrayList<Historial> findByStudentId(String studentId);

  @Query("{ 'studentId' : ?0, 'groupCode' : ?1 }")
  Historial findByStudentIdAndGroupCode(String studentId, String groupCode);
}
