package edu.dosw.dto;

import edu.dosw.model.Course;
import edu.dosw.model.Group;

import java.util.List;

/**
 * Represents the response containing course information.
 * Includes course details and associated groups.
 */
public record CourseResponse(
    /** Unique identifier of the course */
    String id,
    
    /** Course code */
    String code,
    
    /** Name of the course */
    String name,
    
    /** List of groups associated with this course */
    List<Group> groups
) {
    /**
     * Creates a CourseResponse from a Course entity.
     * @param course The course entity to convert
     * @return A new CourseResponse instance
     */
    public static CourseResponse fromModel(Course course) {
        return new CourseResponse(
            course.getId(),
            course.getCode(),
            course.getName(),
            course.getGroups().stream()
                .map(Group::fromModel)
                .toList()
        );
    }
}
