package edu.dosw.repositories;

import edu.dosw.model.Historial;
import edu.dosw.model.enums.HistorialStatus;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistorialRepository extends MongoRepository<Historial, String> {

  @Query("{ 'studentId': ?0, 'year': ?1, 'period': ?2 , 'status': 'ON_GOING'}")
  ArrayList<Historial> findCurrentSessionsByStudentIdAndYearAndPeriod(
      String studentId, String year, String period);

  @Query("{ 'studentId': ?0}")
  ArrayList<Historial> findByStudentId(String studentId);

  @Query("{ 'studentId' : ?0, 'groupCode' : ?1 }")
  Historial findByStudentIdAndGroupCode(String studentId, String groupCode);

    @Query(
            "SELECT DISTINCT h.year, h.period FROM Historial h WHERE h.studentId = :studentId ORDER BY h.year DESC, h.period DESC")
    List<String[]> findDistinctPeriodsByStudentId(@Param("studentId") String studentId);

  @Query("{ 'studentId': ?0, 'status': ?1 }")
  List<Historial> findByStudentIdAndStatus(String studentId, HistorialStatus status);
}
