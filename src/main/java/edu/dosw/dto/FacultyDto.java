package edu.dosw.dto;

import edu.dosw.model.Course;
import java.util.List;

/**
 * Data Transfer Object for faculty information
 *
 * @param facultyName The name of the faculty
 * @param plan The academic plan associated with the faculty
 * @param courses The list of courses offered by the faculty
 */
public record FacultyDto(String facultyName, String plan, List<Course> courses) {}
