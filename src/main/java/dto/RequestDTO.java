package dto;

public record RequestDTO(
        String id,
        String status,
        String createdAt,
        String updatedAt,
        String requestId,
        String originGroup,
        String destinationGroup,
        String description
) {
}
