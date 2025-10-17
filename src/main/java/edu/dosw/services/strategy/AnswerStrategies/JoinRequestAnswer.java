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

@AllArgsConstructor
@Component
public class JoinRequestAnswer implements AnswerStrategy {
  private final GroupService groupService;
  private final HistorialService historialService;
  private final Logger logger = LoggerFactory.getLogger(JoinRequestAnswer.class);

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
