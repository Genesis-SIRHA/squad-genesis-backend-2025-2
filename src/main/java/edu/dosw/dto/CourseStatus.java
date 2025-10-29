package edu.dosw.dto;

import edu.dosw.model.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatus {
  private Course course;
  private String status;
}
