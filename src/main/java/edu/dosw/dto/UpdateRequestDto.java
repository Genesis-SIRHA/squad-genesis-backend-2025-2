package edu.dosw.dto;

import edu.dosw.model.enums.Status;

public record UpdateRequestDto(
        String requestId,
        Status status,
        String answer,
        String managedBy
) {
}