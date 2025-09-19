package dto;

import jakarta.validation.constraints.NotNull;
import model.Group;
import model.Request;

public record RequestDTO(
        @NotNull()
        String id,
        String studentId,
        String type,
        Boolean isExceptional,
        String status,
        String createdAt,
        String requestId,
        String originGroup,
        String destinationGroup,
        String description
) {
    public static RequestDTO fromRequest(Request request) {
        return new RequestDTO(
                request.getId(),
                request.getStudentId(),
                request.getType(),
                request.getIsExceptional(),
                request.getStatus(),
                request.getCreatedAt().toString(),
                request.getRequestDetails().getRequestId(),
                null,
                null,
                request.getDescription()
        );
    }
}
