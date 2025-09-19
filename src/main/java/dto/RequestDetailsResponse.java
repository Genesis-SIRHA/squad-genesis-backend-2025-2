package dto;

import model.RequestDetails;

public record RequestDetailsResponse(
        String id,
        String requestId,
        GroupResponse originGroup,
        GroupResponse destinationGroup,
        String gestedBy,
        String description,
        String answer,
        String answerDate
) {
    public static RequestDetailsResponse fromRequestDetails(RequestDetails requestDetails) {
        return new RequestDetailsResponse(
                requestDetails.getId(),
                requestDetails.getRequestId(),
                GroupResponse.fromModel(requestDetails.getOriginGroup()),
                GroupResponse.fromModel(requestDetails.getDestinationGroup()),
                requestDetails.getGestedBy(),
                requestDetails.getDescription(),
                requestDetails.getAnswer(),
                requestDetails.getAnswerDate().toString()
        );
    }
}
