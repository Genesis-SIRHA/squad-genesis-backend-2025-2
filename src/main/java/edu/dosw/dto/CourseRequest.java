package edu.dosw.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Represents a request to create or update a course.
 * Contains the course code, name, and list of groups.
 *
 * @param code The unique code that identifies the course. Cannot be blank.
 * @param name The name of the course. Cannot be blank.
 * @param groups List of groups associated with this course.
 */
public record CourseRequest(
    @NotBlank(message = "El código del curso es obligatorio")
    String code,
    
    @NotBlank(message = "El nombre del curso es obligatorio")
    String name,
    
    @Valid
    List<GroupRequest> groups
) {}
