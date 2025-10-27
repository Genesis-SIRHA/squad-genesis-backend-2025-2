package edu.dosw.model;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
  @NotBlank private String abbreviation;

  @NotBlank private String courseName;

  private int credits;
  private String semester;
  private List<String> prerequisites;

  public Course(String abbreviation, String courseName) {
    this.abbreviation = abbreviation;
    this.courseName = courseName;
  }
}
