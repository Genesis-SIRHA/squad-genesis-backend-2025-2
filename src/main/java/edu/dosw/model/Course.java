package edu.dosw.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "courses")
public class Course {
  @Id @NotBlank private String abbreviation;

  @NotBlank private String courseName;

  private int credits;

    public Course(String abbreviation, String courseName) {
        this.abbreviation = abbreviation;
        this.courseName = courseName;
    }
  public Course() {}

  public Course(String abbreviation, String courseName, int credits) {
    this.abbreviation = abbreviation;
    this.courseName = courseName;
    this.credits = credits;
  }
}
