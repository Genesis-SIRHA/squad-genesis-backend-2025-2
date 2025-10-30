package edu.dosw.dto;

import jakarta.validation.constraints.Min;

/**
 * Data Transfer Object for updating group information
 *
 * @param professorId The unique identifier of the professor teaching the group
 * @param isLab Indicates whether this group is a laboratory session
 * @param groupNum The group number or identifier
 * @param maxCapacity The maximum number of students allowed in the group
 * @param enrolled The current number of students enrolled in the group
 */
public record UpdateGroupRequest(
    String professorId,
    Boolean isLab,
    String groupNum,
    @Min(value = 1) Integer maxCapacity,
    @Min(value = 0) Integer enrolled) {}
