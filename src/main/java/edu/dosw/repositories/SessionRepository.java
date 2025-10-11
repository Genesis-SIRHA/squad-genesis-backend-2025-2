package edu.dosw.repositories;

import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.ArrayList;

public interface SessionRepository extends MongoRepository<Session, String> {
  @Query("{  'groupCode': ?0 }")
  ArrayList<Session> findByGroupCode(String groupCode);

  @Query("{  'sessionId': ?0 }")
  Session findBySessionId(String sessionId);

  @Query("{  'classroomName': ?0, 'slot': ?1, 'day': ?2, 'year': ?3, 'period': ?4 }")
  Session getSessionByScheduleAndClassroom(
          String classRoom, Integer slot, DayOfWeek day, String year, String period);

  @Query("{  'slot': ?0, 'day': ?1, 'year': ?2, 'period': ?3 }")
  Session getSessionByScheduleAndGroupCode(
          String groupCode, Integer slot, DayOfWeek day, String year, String period);
}
