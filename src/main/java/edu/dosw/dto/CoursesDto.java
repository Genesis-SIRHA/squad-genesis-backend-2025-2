package edu.dosw.dto;

import edu.dosw.model.Course;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for courses organized by category
 *
 * @param courses Map containing course categories as keys and lists of courses as values
 */
public record CoursesDto(Map<String, List<Course>> courses) {}
