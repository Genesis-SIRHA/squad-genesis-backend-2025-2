package dto;

import model.Request;
import model.RequestDetails;

public record RequestResponse(
        String id,
        String studentId,
        String status,
        String createdAt,
        String type,
        Boolean isExceptional,
        RequestDetailsResponse requestDetails) {

    public static RequestResponse fromRequest(Request request) {
        return new RequestResponse(
            request.getId(),
            request.getStatus(),
            request.getCreatedAt().toString(),
            request.getType(),
            request.getStudentId(),
            request.getIsExceptional(),
            RequestDetailsResponse.fromRequestDetails(request.getRequestDetails())
        );
    }
}
