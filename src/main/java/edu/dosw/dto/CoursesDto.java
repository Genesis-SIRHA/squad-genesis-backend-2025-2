package edu.dosw.dto;

import edu.dosw.model.Course;

import java.util.List;
import java.util.Map;

public record CoursesDto(
        Map<String,List<Course>> courses
) {
}
