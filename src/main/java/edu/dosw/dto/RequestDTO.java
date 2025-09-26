package edu.dosw.dto;

import jakarta.validation.constraints.NotNull;
import edu.dosw.model.Request;

import java.util.UUID;

public record RequestDTO(
        String id,
        @NotNull String studentId,
        String type,
        Boolean isExceptional,
        String status,
        String description,
        String originGroupId,
        String destinationGroupId,
        String answer,
        String managedBy
) { 
    public RequestDTO {
        if (isExceptional == null) isExceptional = false;
        if (status == null) status = "PENDING";
    }
    public static RequestDTO fromRequest(Request request) {
        return new RequestDTO(
                request.getId(),
                request.getStudentId(),
                request.getType(),
                request.getIsExceptional(),
                request.getStatus(),
                request.getDescription(),
                request.getOriginGroupId() != null ? request.getOriginGroupId() : null,
                request.getDestinationGroupId() != null ? request.getDestinationGroupId(): null,
                request.getAnswer(),
                request.getGestedBy()
        );
    }
    
    public Request toEntity() {
        Request request = new Request();
        request.setId(this.id != null ? this.id : UUID.randomUUID().toString());
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
