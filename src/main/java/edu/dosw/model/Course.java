package edu.dosw.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

@Data
@Document(collection = "courses")
public class Course {
    @Id
    @NotBlank
    private String abbreviation;

    @NotBlank
    private String courseName;

    public Course() {
    }

    public Course(String abbreviation, String courseName) {
        this.abbreviation = abbreviation;
        this.courseName = courseName;
    }
}