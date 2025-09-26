package edu.dosw.repositories;

import edu.dosw.model.Historial;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.ArrayList;

public interface HistorialRepository extends MongoRepository<Historial, String> {

    @Query("{ 'studentId': ?0, 'year': ?1, 'period': ?2 , 'status': 'ON_GOING'}")
    ArrayList<Historial> findCurrentSessionsByStudentIdAndYearAndPeriod(String studentId, String year, String period);
}
