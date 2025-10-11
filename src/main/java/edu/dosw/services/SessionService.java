package edu.dosw.services;

import edu.dosw.dto.SessionDTO;
import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import edu.dosw.repositories.SessionRepository;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to academic sessions. Provides methods for
 * retrieving session information by group code.
 */
@AllArgsConstructor
@Service
public class SessionService {
  private final SessionRepository sessionRepository;
  private final PeriodService periodService;
  private final ValidatorService validatorService;
  private final Logger logger = LoggerFactory.getLogger(SessionService.class);

  public List<Session> getSessionsByGroupCode(String groupCode) {
    ArrayList<Session> groupSchedule = sessionRepository.findByGroupCode(groupCode);
    if(groupSchedule == null){
      logger.error("Group schedule not found by GroupCode: {}", groupCode);
      throw new IllegalArgumentException("Group schedule not found by GroupCode: " + groupCode);
    }
    return groupSchedule;
  }

  public Session getSessionBySessionId(String sessionId){
    Session session = sessionRepository.findBySessionId(sessionId);
    if(session == null){
      logger.error("Session not found by sessionId: {}", sessionId);
      throw new IllegalArgumentException("Session not found by sessionId: " + sessionId);
    }
    return session;
  }

  public Session getSessionsByScheduleAndClassroom(SessionDTO sessionDto){
    String classroom = sessionDto.classroomName();
    Integer slot = sessionDto.slot();
    DayOfWeek day = sessionDto.day();
    String year = periodService.getYear();
    String period = periodService.getPeriod();
    Session session = sessionRepository.getSessionByScheduleAndClassroom(classroom,slot,day,year, period);
    if(session == null){
      logger.error("Session not found by scheduled and classroom: {} , {}", day, classroom);
      throw new IllegalArgumentException("Session not found by sessionId: " + day + ", "+ classroom);
    }
    return session;
  }

  public Session getSessionsByScheduleAndGroupCode(SessionDTO sessionDTO){
    String groupCode = sessionDTO.groupCode();
    Integer slot = sessionDTO.slot();
    DayOfWeek day = sessionDTO.day();
    String year = periodService.getYear();
    String period = periodService.getPeriod();

    Session session = sessionRepository.getSessionByScheduleAndGroupCode(groupCode, slot,day,year, period);

    if(session == null){
      logger.error("Session not found by scheduled and classroom: {} en la franja {}", day, slot);
      throw new IllegalArgumentException("Session not found by sessionId: " + day +" en la franja "+ slot);
    }
    return session;
  }

  public Session createSession(SessionDTO sessiondto) {
      validatorService.validateCreateSession(sessiondto);

      Session session = new Session.SessionBuilder()
              .groupCode(sessiondto.groupCode())
              .classroomName(sessiondto.classroomName())
              .slot(sessiondto.slot())
              .day(sessiondto.day())
              .year(periodService.getYear())
              .period(periodService.getPeriod())
              .build();
      try{
        return sessionRepository.save(session);
      }catch(Exception e){
        logger.error("Failed to create session: {}", e.getMessage());
        throw new RuntimeException("Failed to create session: " + e.getMessage());
      }
  }

  public Session updateSession(String sessionId, SessionDTO sessiondto) {
    Session session = getSessionBySessionId(sessionId);
    validatorService.validateUpdateSession(sessiondto, session.getYear(), session.getPeriod());

    if (sessiondto.groupCode() != null) session.setGroupCode(sessiondto.groupCode());
    if (sessiondto.day() != null) session.setDay(sessiondto.day());
    if (sessiondto.classroomName() != null) session.setClassroomName(sessiondto.classroomName());
    if (sessiondto.slot() != null) session.setSlot(sessiondto.slot());

    try{
      return sessionRepository.save(session);
    }catch (Exception e){
      logger.error("An unexpected error has occurred updating a session: {}",e.getMessage());
      throw new RuntimeException("An unexpected error has occurred updating a session:"+e.getMessage());
    }

  }

  public Session deleteSession(String sessionId) {
    Session session = getSessionBySessionId(sessionId);
    validatorService.validateDeleteSession(session);
    try{
      sessionRepository.delete(session);
      return  session;
    }catch (Exception e){
      logger.error("An unexpected error has occurred deleting a session : {}", e.getMessage());
      throw new RuntimeException("An unexpected error has occurred updating a session: "+ e.getMessage());
    }
  }

}
