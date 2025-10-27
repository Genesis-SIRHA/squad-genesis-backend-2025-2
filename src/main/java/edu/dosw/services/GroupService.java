package edu.dosw.services;

import edu.dosw.dto.CreationGroupRequest;
import edu.dosw.dto.HistorialDTO;
import edu.dosw.dto.SessionDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.Session;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.repositories.GroupRepository;
import edu.dosw.services.Validators.GroupValidator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to groups. Provides methods for retrieving and
 * managing group information.
 */
@AllArgsConstructor
@Service
public class GroupService {
  private final Logger logger = LoggerFactory.getLogger(GroupService.class);
  private final FacultyService facultyService;
  private final GroupRepository groupRepository;
  private final PeriodService periodService;
  private final SessionService sessionService;
  private final HistorialService historialService;
  private final GroupValidator groupValidator;

  /**
   * Retrieves all groups associated with a specific course abbreviation.
   *
   * @param abbreviation the course abbreviation to filter groups by
   * @return a list of groups associated with the given course abbreviation
   */
  public List<Group> getAllGroupsByCourseAbbreviation(String abbreviation) {
    return groupRepository.findAllByCourseId(abbreviation);
  }

  /**
   * Retrieves a group by its unique group code.
   *
   * @param groupCode the unique code of the group to retrieve
   * @return an Optional containing the group if found, or empty if not found
   */
  public Group getGroupByGroupCode(String groupCode) {
    Group group = groupRepository.findByGroupCode(groupCode).orElse(null);
    if (group == null) {
      logger.error("Group not found by id {}", groupCode);
      throw new IllegalArgumentException("Group not found: " + groupCode);
    }
    return group;
  }

  /**
   * Creates a new group using the provided group request data.
   *
   * @param groupRequest the group data transfer object containing group information
   * @return the newly created group entity
   */
  public Group createGroup(CreationGroupRequest groupRequest, String facultyName, String plan) {
    Course course =
        facultyService.findCourseByAbbreviation(groupRequest.abbreviation(), facultyName, plan);
    if (course == null) {
      logger.error("Faculty not found: " + groupRequest.abbreviation());
      throw new BusinessException("Faculty not found: " + groupRequest.abbreviation());
    }

    Group group =
        new Group.GroupBuilder()
            .groupCode(groupRequest.groupCode())
            .abbreviation(groupRequest.abbreviation())
            .year(periodService.getYear())
            .period(periodService.getPeriod())
            .professorId(groupRequest.teacherId())
            .isLab(groupRequest.isLab())
            .groupNum(groupRequest.groupNum())
            .enrolled(groupRequest.enrolled())
            .maxCapacity(groupRequest.maxCapacity())
            .build();

    return groupRepository.save(group);
  }

  public Group updateGroup(String groupCode, UpdateGroupRequest groupRequest) {
    Group group = groupRepository.findByGroupCode(groupCode).orElse(null);
    if (group == null) {
      logger.error("Group not found: {}", groupCode);
      throw new BusinessException("Group not found: " + groupCode);
    }
    if (groupRequest.professorId() != null) group.setProfessorId(groupRequest.professorId());
    if (groupRequest.isLab() != null) group.setLab(groupRequest.isLab());
    if (groupRequest.groupNum() != null) group.setGroupNum(groupRequest.groupNum());
    if (groupRequest.maxCapacity() != null) group.setMaxCapacity(groupRequest.maxCapacity());
    if (groupRequest.enrolled() != null) group.setEnrolled(groupRequest.enrolled());

    return groupRepository.save(group);
  }

  public Group deleteGroup(String groupCode) {
    Group group = getGroupByGroupCode(groupCode);
    try {
      groupRepository.delete(group);
      sessionService.deleteSessionsByGroupCode(groupCode);
      return group;
    } catch (Exception e) {
      logger.error("Failed to delete group: " + e.getMessage());
      throw new BusinessException("Failed to delete group");
    }
  }

  public Session addSession(SessionDTO sessiondto) {
    getGroupByGroupCode(sessiondto.groupCode());
    return sessionService.createSession(sessiondto);
  }

  public Session updateSession(String sessionId, SessionDTO sessiondto) {
    getGroupByGroupCode(sessiondto.groupCode());
    return sessionService.updateSession(sessionId, sessiondto);
  }

  public Session getSessionBySessionId(String sessionId) {
    return sessionService.getSessionBySessionId(sessionId);
  }

  public List<Session> getSessionsByGroupCode(String groupCode) {
    return sessionService.getSessionsByGroupCode(groupCode);
  }

  public Session deleteSession(String sessionId) {
    return sessionService.deleteSession(sessionId);
  }

  public Group deleteSessionsFromGroup(String groupId) {
    Group group = getGroupByGroupCode(groupId);
    sessionService.deleteSessionsByGroupCode(groupId);
    return group;
  }

  public Group addStudent(String groupCode, String studentId) {
    Group group = getGroupByGroupCode(groupCode);
    groupValidator.validateAddStudentToGroup(group, studentId);
    try {
      group.setEnrolled(group.getEnrolled() + 1);
      groupRepository.save(group);

      HistorialDTO historialDTO = new HistorialDTO(studentId, groupCode, HistorialStatus.ON_GOING);
      historialService.addHistorial(historialDTO);
    } catch (Exception e) {
      historialService.updateHistorial(studentId, groupCode, HistorialStatus.ON_GOING);
    }
    return group;
  }

  public Group deleteStudent(String groupCode, String studentId) {
    Group group = getGroupByGroupCode(groupCode);

    if (!group.getYear().equals(periodService.getYear())
        || !group.getPeriod().equals(periodService.getPeriod())) {
      logger.error(
          "The historial period and year does not match the one from the group: {} != {}",
          group.getPeriod(),
          periodService.getPeriod());
      throw new IllegalArgumentException(
          "The historial period and year does not match the one from the group"
              + group.getPeriod()
              + " != "
              + periodService.getPeriod());
    }

    group.setEnrolled(group.getEnrolled() - 1);
    groupRepository.save(group);

    try {
      historialService.updateHistorial(studentId, groupCode, HistorialStatus.CANCELLED);
    } catch (Exception e) {
      logger.error("Failed to update historial: " + e.getMessage());
      throw new BusinessException("Failed to update historial" + e.getMessage());
    }
    return group;
  }
}
