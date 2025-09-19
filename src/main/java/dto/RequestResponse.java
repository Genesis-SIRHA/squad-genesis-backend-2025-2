package dto;

import model.Request;

public record RequestResponse(
        String id,
        String studentId,
        String status,
        String createdAt,
        String type) {

    public static RequestResponse fromRequest(Request request) {
        return new RequestResponse(
            request.getId(),
            request.getStatus(),
            request.getCreatedAt().toString(),
        );
    }
}
