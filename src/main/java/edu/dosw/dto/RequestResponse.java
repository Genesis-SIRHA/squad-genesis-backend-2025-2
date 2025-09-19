package edu.dosw.dto;

import edu.dosw.model.Request;

public record RequestResponse(
        String id,
        String studentId,
        String status,
        String createdAt,
        String type,
        Boolean isExceptional,
        String description,
        GroupResponse originGroup,
        GroupResponse destinationGroup,
        String answer,
        String managedBy,
        String answerAt) {

    public static RequestResponse fromRequest(Request request) {
        return new RequestResponse(
            request.getId(),
            request.getStudentId(),
            request.getStatus(),
            request.getCreatedAt().toString(),
            request.getType(),
            request.getIsExceptional(),
            request.getDescription(),
            request.getOriginGroup() != null ? GroupResponse.fromModel(request.getOriginGroup()) : null,
            request.getDestinationGroup() != null ? GroupResponse.fromModel(request.getDestinationGroup()) : null,
            request.getAnswer(),
            request.getManagedBy(),
            request.getAnswerAt() != null ? request.getAnswerAt().toString() : null
        );
    }
}
