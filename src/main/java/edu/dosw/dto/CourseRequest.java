package edu.dosw.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Represents a request to create or update a course.
 * Contains the course code, name, and list of groups.
 */
public record CourseRequest(
    /** The unique code that identifies the course. Cannot be blank. */
    @NotBlank(message = "El código del curso es obligatorio")
    String code,
    
    /** The name of the course. Cannot be blank. */
    @NotBlank(message = "El nombre del curso es obligatorio")
    String name,
    
    /** List of groups associated with this course. */
    @Valid
    List<GroupRequest> groups
) {}
