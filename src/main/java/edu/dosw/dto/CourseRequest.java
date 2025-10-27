package edu.dosw.dto;

import edu.dosw.model.Course;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Represents a request to create or update a course. Contains the course abbreviation, fullName,
 * and list of groups.
 */
public record CourseRequest(
    /** The unique abbreviation that identifies the course. Cannot be blank. */
    @NotBlank(message = "El código del curso es obligatorio") String abbreviation,

    /** The fullName of the course. Cannot be blank. */
    @NotBlank(message = "El nombre del curso es obligatorio") String courseName,
    int credits,
    String facultyName,
    String plan,
    String semester,
    List<String> prerequisites
    ) {

  public Course toEntity() {
    return new Course(this.abbreviation(), this.courseName(), this.credits(), this.semester(), this.prerequisites());
  }
}
