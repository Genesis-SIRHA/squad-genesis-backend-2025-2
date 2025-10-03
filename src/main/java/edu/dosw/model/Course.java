package edu.dosw.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "courses")
public class Course {
  @Id @NotBlank private String abbreviation;

  @NotBlank private String courseName;

  private int credits;

  public Course() {}

  public Course(String abbreviation, String courseName, int credits) {
    this.abbreviation = abbreviation;
    this.courseName = courseName;
    this.credits = credits;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public int getCredits() {
    return credits;
  }

  public void setCredits(int credits) {
    this.credits = credits;
  }
}
