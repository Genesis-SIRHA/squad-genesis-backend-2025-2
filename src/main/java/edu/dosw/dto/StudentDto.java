package edu.dosw.dto;

import edu.dosw.model.enums.AcademicGrade;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StudentDto(
    /**
     * Data Transfer Object for session information
     *
     * @param groupCode The unique code identifying the group for the session
     * @param classroomName The name of the classroom where the session takes place
     * @param slot The time slot of the session (1-7)
     * @param day The day of the week when the session occurs
     */
    @NotBlank String identityDocument,
    String fullName,
    String plan,
    String facultyName,
    AcademicGrade academicGrade,
    @Min(1) Integer semester) {}
