package edu.dosw.services;

import edu.dosw.dto.SessionDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Session;
import edu.dosw.model.enums.DayOfWeek;
import edu.dosw.repositories.SessionRepository;
import edu.dosw.services.Validators.SessionValidator;
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
  private final SessionValidator sessionValidator;
  private final Logger logger = LoggerFactory.getLogger(SessionService.class);

  /**
   * Retrieves all sessions for a specific group
   *
   * @param groupCode The unique code identifying the group
   * @return List of sessions for the specified group
   * @throws ResourceNotFoundException If no sessions are found for the group
   */
  public List<Session> getSessionsByGroupCode(String groupCode) {
    ArrayList<Session> groupSchedule = sessionRepository.findByGroupCode(groupCode);
    if (groupSchedule == null) {
      logger.error("Group schedule not found by GroupCode: {}", groupCode);
      throw new ResourceNotFoundException("Group schedule not found by GroupCode: " + groupCode);
    }
    return groupSchedule;
  }

  /**
   * Retrieves a specific session by its unique identifier
   *
   * @param sessionId The unique identifier of the session
   * @return The session with the specified ID
   * @throws ResourceNotFoundException If no session is found with the given ID
   */
  public Session getSessionBySessionId(String sessionId) {
    Session session = sessionRepository.findBySessionId(sessionId);
    if (session == null) {
      logger.error("Session not found by sessionId: {}", sessionId);
      throw new ResourceNotFoundException("Session not found by sessionId: " + sessionId);
    }
    return session;
  }

  /**
   * Retrieves a session by its schedule and classroom
   *
   * @param sessionDto The DTO containing session schedule and classroom information
   * @return The session matching the criteria
   * @throws ResourceNotFoundException If no session is found with the given criteria
   */
  public Session getSessionsByScheduleAndClassroom(SessionDTO sessionDto) {
    String classroom = sessionDto.classroomName();
    Integer slot = sessionDto.slot();
    DayOfWeek day = sessionDto.day();
    String year = periodService.getYear();
    String period = periodService.getPeriod();
    Session session =
        sessionRepository.getSessionByScheduleAndClassroom(classroom, slot, day, year, period);
    if (session == null) {
      logger.error("Session not found by scheduled and classroom: {} , {}", day, classroom);
      throw new ResourceNotFoundException(
          "Session not found by sessionId: " + day + ", " + classroom);
    }
    return session;
  }

  /**
   * Retrieves a session by its schedule and group code
   *
   * @param sessionDTO The DTO containing session schedule and group code information
   * @return The session matching the criteria
   * @throws ResourceNotFoundException If no session is found with the given criteria
   */
  public Session getSessionsByScheduleAndGroupCode(SessionDTO sessionDTO) {
    String groupCode = sessionDTO.groupCode();
    Integer slot = sessionDTO.slot();
    DayOfWeek day = sessionDTO.day();
    String year = periodService.getYear();
    String period = periodService.getPeriod();

    Session session =
        sessionRepository.getSessionByScheduleAndGroupCode(groupCode, slot, day, year, period);

    if (session == null) {
      logger.error("Session not found by scheduled and classroom: {} en la franja {}", day, slot);
      throw new ResourceNotFoundException(
          "Session not found by sessionId: " + day + " en la franja " + slot);
    }
    return session;
  }

  /**
   * Creates a new session
   *
   * @param sessiondto The DTO containing session creation data
   * @return The created session
   * @throws BusinessException If session creation fails
   */
  public Session createSession(SessionDTO sessiondto) {
    sessionValidator.validateCreateSession(sessiondto);

    Session session =
        new Session.SessionBuilder()
            .groupCode(sessiondto.groupCode())
            .classroomName(sessiondto.classroomName())
            .slot(sessiondto.slot())
            .day(sessiondto.day())
            .year(periodService.getYear())
            .period(periodService.getPeriod())
            .build();
    try {
      return sessionRepository.save(session);
    } catch (Exception e) {
      logger.error("Failed to create session: {}", e.getMessage());
      throw new BusinessException("Failed to create session: " + e.getMessage());
    }
  }

  /**
   * Updates an existing session
   *
   * @param sessionId The unique identifier of the session to update
   * @param sessiondto The DTO containing updated session data
   * @return The updated session
   * @throws BusinessException If session update fails
   */
  public Session updateSession(String sessionId, SessionDTO sessiondto) {
    Session session = getSessionBySessionId(sessionId);
    sessionValidator.validateUpdateSession(sessiondto, session.getYear(), session.getPeriod());

    if (sessiondto.groupCode() != null) session.setGroupCode(sessiondto.groupCode());
    if (sessiondto.day() != null) session.setDay(sessiondto.day());
    if (sessiondto.classroomName() != null) session.setClassroomName(sessiondto.classroomName());
    if (sessiondto.slot() != null) session.setSlot(sessiondto.slot());

    try {
      return sessionRepository.save(session);
    } catch (Exception e) {
      logger.error("An unexpected error has occurred updating a session: {}", e.getMessage());
      throw new BusinessException(
          "An unexpected error has occurred updating a session:" + e.getMessage());
    }
  }

  /**
   * Deletes a specific session
   *
   * @param sessionId The unique identifier of the session to delete
   * @return The deleted session
   * @throws BusinessException If session deletion fails
   */
  public Session deleteSession(String sessionId) {
    Session session = getSessionBySessionId(sessionId);
    sessionValidator.validateDeleteSession(session);
    try {
      sessionRepository.delete(session);
      return session;
    } catch (Exception e) {
      logger.error("An unexpected error has occurred deleting a session : {}", e.getMessage());
      throw new BusinessException(
          "An unexpected error has occurred updating a session: " + e.getMessage());
    }
  }

  /**
   * Deletes all sessions associated with a specific group
   *
   * @param groupCode The unique code identifying the group
   */
  public void deleteSessionsByGroupCode(String groupCode) {
    List<Session> sessions = getSessionsByGroupCode(groupCode);
    for (Session session : sessions) {
      sessionRepository.delete(session);
    }
  }
}
