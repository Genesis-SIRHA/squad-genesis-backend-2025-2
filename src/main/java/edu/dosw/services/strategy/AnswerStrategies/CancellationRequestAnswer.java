package edu.dosw.services.strategy.AnswerStrategies;

import edu.dosw.dto.UpdateGroupRequest;
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
public class CancellationRequestAnswer implements AnswerStrategy {
  private final GroupService groupService;
  private final HistorialService historialService;
  private final Logger logger = LoggerFactory.getLogger(SwapRequestAnswer.class);

  public void answerRequest(Request request) {
    Group group = groupService.getGroupByGroupCode(request.getOriginGroupId());

    try {
      UpdateGroupRequest groupRequest =
              new UpdateGroupRequest(
                      group.getProfessorId(),
                      group.isLab(),
                      group.getGroupNum(),
                      group.getMaxCapacity(),
                      group.getEnrolled() - 1);

      groupService.updateGroup(group.getGroupCode(), groupRequest);

      historialService.updateHistorial(
              request.getStudentId(), request.getOriginGroupId(), HistorialStatus.CANCELLED);

    } catch (Exception e) {
      logger.error("Failed to answer request: {}", e.getMessage());
      throw new RuntimeException("Failed to answer request: " + e.getMessage());
    }
  }
}
