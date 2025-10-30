package edu.dosw.dto;

import edu.dosw.model.enums.DayOfWeek;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Data Transfer Object for session information
 *
 * @param groupCode The unique code identifying the group for the session
 * @param classroomName The name of the classroom where the session takes place
 * @param slot The time slot of the session (1-7)
 * @param day The day of the week when the session occurs
 */
public record SessionDTO(
    String groupCode,
    String classroomName,
    @Min(value = 1) @Max(value = 7) Integer slot,
    DayOfWeek day) {}
