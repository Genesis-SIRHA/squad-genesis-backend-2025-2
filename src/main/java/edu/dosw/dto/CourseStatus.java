package edu.dosw.dto;

import edu.dosw.model.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * Data Transfer Object representing a course with its status
 *
 * @param course The course entity
 * @param status The status of the course (e.g., approved, pending, failed)
 */
public class CourseStatus {
  private Course course;
  private String status;
}
