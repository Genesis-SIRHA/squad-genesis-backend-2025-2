package edu.dosw.dto;

import edu.dosw.model.Course;

import java.util.List;

public record FacultyDto(String facultyName, String plan, List<Course> courses) {
}
