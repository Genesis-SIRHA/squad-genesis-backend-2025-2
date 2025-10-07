package edu.dosw.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "faculties")
public class Faculty {
  @Id
  @Field("facultyName")
  private String facultyName;
  @Id
  @Field("plan")
  private String plan;
  
  @Field("courses")
  private List<Course> courses = new ArrayList<>();

  public Faculty(String facultyName, String plan, List<Course> courses) {
    this.facultyName = facultyName;
    this.plan = plan;
    if (courses != null) {
      this.courses = new ArrayList<>(courses);
    }
  }
}
