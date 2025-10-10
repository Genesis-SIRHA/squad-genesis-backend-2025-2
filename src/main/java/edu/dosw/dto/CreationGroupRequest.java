package edu.dosw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a request to create or update a group. Contains group details like abbreviation,
 * professor, maxCapacity, and enrolled students.
 */
public record CreationGroupRequest(
    @NotBlank String groupCode,
    @NotBlank String abbreviation,
    @NotBlank String teacherId,
    @NotNull Boolean isLab,
    String groupNum,
    @Min(value = 1) int maxCapacity,
    @Min(value = 0) int enrolled) {
}
