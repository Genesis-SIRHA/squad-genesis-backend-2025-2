package edu.dosw.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/** Represents a faculty in the academic system with its associated courses and academic plan */
@Data
@NoArgsConstructor
@Document(collection = "faculties")
public class Faculty {
  @Id private String id;

  @Field("facultyName")
  private String facultyName;

  @Field("plan")
  private String plan;

  @Field("courses")
  private List<Course> courses = new ArrayList<>();

  /**
   * Constructs a Faculty with name, plan, and courses
   *
   * @param facultyName The name of the faculty
   * @param plan The academic plan associated with the faculty
   * @param courses The list of courses offered by the faculty
   */
  public Faculty(String facultyName, String plan, List<Course> courses) {
    this.facultyName = facultyName;
    this.plan = plan;
    if (courses != null) {
      this.courses = new ArrayList<>(courses);
    }
  }
}
