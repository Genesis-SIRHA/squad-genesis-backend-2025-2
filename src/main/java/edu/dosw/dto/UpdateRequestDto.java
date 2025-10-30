package edu.dosw.dto;

import edu.dosw.model.enums.RequestStatus;

/**
 * Data Transfer Object for updating request information
 *
 * @param requestId The unique identifier of the request to update
 * @param status The updated status of the request
 * @param answer The response or answer to the request
 * @param managedBy The identifier of the user managing the request
 */
public record UpdateRequestDto(
    String requestId, RequestStatus status, String answer, String managedBy) {}
