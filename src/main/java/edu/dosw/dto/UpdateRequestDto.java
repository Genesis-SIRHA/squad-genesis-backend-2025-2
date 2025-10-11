package edu.dosw.dto;

import edu.dosw.model.enums.RequestStatus;

public record UpdateRequestDto(
        String requestId, RequestStatus status, String answer, String managedBy) {
}
