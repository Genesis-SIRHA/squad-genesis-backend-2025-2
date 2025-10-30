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
 * Strategy implementation for handling swap request answers Processes student transfers between
 * groups and updates enrollment counts
 */
@AllArgsConstructor
@Component
public class SwapRequestAnswer implements AnswerStrategy {
  private final GroupService groupService;
  private final HistorialService historialService;
  private final Logger logger = LoggerFactory.getLogger(SwapRequestAnswer.class);

  /**
   * Processes a swap request by moving the student from origin group to destination group and
   * updating enrollment counts and historial status for both groups
   *
   * @param request The swap request to process
   * @throws BusinessException If the destination group is full or the swap process fails
   */
  public void answerRequest(Request request) {
    Group originGroup = groupService.getGroupByGroupCode(request.getOriginGroupId());
    UpdateGroupRequest originGroupRequest =
        new UpdateGroupRequest(
            originGroup.getProfessorId(),
            originGroup.isLab(),
            originGroup.getGroupNum(),
            originGroup.getMaxCapacity(),
            originGroup.getEnrolled() - 1);

    Group destinationGroup = groupService.getGroupByGroupCode(request.getDestinationGroupId());
    UpdateGroupRequest destinationGroupRequest =
        new UpdateGroupRequest(
            destinationGroup.getProfessorId(),
            destinationGroup.isLab(),
            destinationGroup.getGroupNum(),
            destinationGroup.getMaxCapacity(),
            destinationGroup.getEnrolled() + 1);

    try {
      if (destinationGroup.getEnrolled() >= destinationGroup.getMaxCapacity()) {
        logger.error("Destination group {} is full", destinationGroup.getGroupCode());
        throw new BusinessException(
            "Destination" + destinationGroup.getGroupCode() + "group is full");
      }
      groupService.updateGroup(originGroup.getGroupCode(), originGroupRequest);
      groupService.updateGroup(destinationGroup.getGroupCode(), destinationGroupRequest);

      HistorialDTO historialDTO =
          new HistorialDTO(
              request.getStudentId(), request.getDestinationGroupId(), HistorialStatus.ON_GOING);

      historialService.addHistorial(historialDTO);
      historialService.updateHistorial(
          request.getStudentId(), request.getOriginGroupId(), HistorialStatus.SWAPPED);
    } catch (Exception e) {
      logger.error("Failed to answer request: {}", e.getMessage());
      throw new BusinessException("Failed to answer request: " + e.getMessage());
    }
  }
}
