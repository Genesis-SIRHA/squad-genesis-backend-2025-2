package edu.dosw.model;

import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "courses")
public class Course {
    @abbreviation
    private String abbreviation;
    @NotBlank
    private String courseName;

    public Course() {}

    public Course(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.courseName = name;
    }

    public String getId() { return abbreviation; }
    public void setId(String abbreviation) { this.abbreviation = abbreviation; }

    public String getName() { return courseName; }
    public void setName(String name) { this.courseName = name; }
}