package edu.dosw.services.strategy.AnswerStrategies;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.dto.UpdateGroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.services.GroupService;
import edu.dosw.services.HistorialService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for handling join request answers Processes student enrollments into
 * groups and updates enrollment counts
 */
@AllArgsConstructor
@Component
public class JoinRequestAnswer implements AnswerStrategy {
  private final GroupService groupService;
  private final HistorialService historialService;
  private final Logger logger = LoggerFactory.getLogger(JoinRequestAnswer.class);

  /**
   * Processes a join request by adding the student to the destination group and updating enrollment
   * counts and historial status
   *
   * @param request The join request to process
   * @throws BusinessException If the group is full or the join process fails
   */
  public void answerRequest(Request request) {
    Group group = groupService.getGroupByGroupCode(request.getDestinationGroupId());
    if (group.getEnrolled() >= group.getMaxCapacity()) {
      logger.error("Destination group {} is full", group.getGroupCode());
      throw new BusinessException("Destination" + group.getGroupCode() + "group is full");
    }

    try {
      UpdateGroupRequest groupRequest =
          new UpdateGroupRequest(
              group.getProfessorId(),
              group.isLab(),
              group.getGroupNum(),
              group.getMaxCapacity(),
              group.getEnrolled() + 1);
      groupService.updateGroup(group.getGroupCode(), groupRequest);

      HistorialDTO historialDTO =
          new HistorialDTO(
              request.getStudentId(), request.getDestinationGroupId(), HistorialStatus.ON_GOING);

      try {
        historialService.addHistorial(historialDTO);
      } catch (Exception e) {
        historialService.updateHistorial(
            request.getStudentId(), request.getDestinationGroupId(), HistorialStatus.ON_GOING);
      }

    } catch (Exception e) {
      logger.error("Failed to answer request: {}", e.getMessage());
      throw new BusinessException("Failed to answer request: " + e.getMessage());
    }
  }
}
