package edu.dosw.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
  @NotBlank private String abbreviation;

  @NotBlank private String courseName;

  private int credits;

  public Course(String abbreviation, String courseName) {
    this.abbreviation = abbreviation;
    this.courseName = courseName;
  }
}
