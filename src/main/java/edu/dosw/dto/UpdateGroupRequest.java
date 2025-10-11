package edu.dosw.dto;

import jakarta.validation.constraints.Min;

public record UpdateGroupRequest(
    String professorId,
    Boolean isLab,
    String groupNum,
    @Min(value = 1) Integer maxCapacity,
    @Min(value = 0) Integer enrolled) {}
