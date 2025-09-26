package edu.dosw.model;

import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

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
}