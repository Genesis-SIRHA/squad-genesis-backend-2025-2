package edu.dosw.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for handling request-related operations. Represents a request with all its
 * associated details including student information, request type, status, and related groups.
 *
 * @param studentId ID of the student making the request. Cannot be null.
 * @param type Type of the request (e.g., group change, special permission).
 * @param description Detailed description of the request.
 * @param originGroupId ID of the source group in case of group change requests.
 * @param destinationGroupId ID of the target group in case of group change requests.
 */
public record CreateRequestDto(
    @NotNull String studentId,
    String type,
    String description,
    String originGroupId,
    String destinationGroupId
) {}
