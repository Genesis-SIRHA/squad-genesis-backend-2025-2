package edu.dosw.dto;

import edu.dosw.model.Request;
import edu.dosw.model.enums.Status;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Data Transfer Object for handling request-related operations. Represents a request with all its
 * associated details including student information, request type, status, and related groups.
 *
 * @param id Unique identifier for the request. Auto-generated if not provided.
 * @param studentId ID of the student making the request. Cannot be null.
 * @param type Type of the request (e.g., group change, special permission).
 * @param isExceptional Indicates if this is an exceptional request. Defaults to false.
 * @param status Current status of the request. Defaults to "PENDING".
 * @param description Detailed description of the request.
 * @param originGroupId ID of the source group in case of group change requests.
 * @param destinationGroupId ID of the target group in case of group change requests.
 * @param answer Response or resolution provided for the request.
 * @param managedBy ID of the staff member handling this request.
 */
public record RequestDTO(
    String id,
    @NotNull String studentId,
    String type,
    Boolean isExceptional,
    Status status,
    String description,
    String originGroupId,
    String destinationGroupId,
    String answer,
    String managedBy) {
  public RequestDTO {
    if (isExceptional == null) isExceptional = false;
    if (status == null) status = Status.PENDING;
  }

  /**
   * Creates a RequestDTO from a Request entity.
   *
   * @param request The Request entity to convert
   * @return A new RequestDTO populated with data from the Request entity
   */
  public static RequestDTO fromRequest(Request request) {
    return new RequestDTO(
        request.getRequestId(),
        request.getStudentId(),
        request.getType(),
        request.getIsExceptional(),
        request.getStatus(),
        request.getDescription(),
        request.getOriginGroupId() != null ? request.getOriginGroupId() : null,
        request.getDestinationGroupId() != null ? request.getDestinationGroupId() : null,
        request.getAnswer(),
        request.getGestedBy());
  }

  /**
   * Converts this DTO to a Request entity. Generates a new UUID for the request if no ID is
   * provided.
   *
   * @return A new Request entity populated with this DTO's data
   */
  public Request toEntity() {
    Request request = new Request();
    request.setRequestId(this.id != null ? this.id : UUID.randomUUID().toString());
    request.setStudentId(this.studentId);
    request.setType(this.type);
    request.setIsExceptional(this.isExceptional);
    request.setStatus(this.status);
    request.setDescription(this.description);
    request.setAnswer(this.answer);
    request.setGestedBy(this.managedBy);
    return request;
  }
}
