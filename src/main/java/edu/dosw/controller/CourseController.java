package edu.dosw.controller;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Course;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.dosw.services.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Controller", description = "APIs for managing courses and groups")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieves a list of all registered courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieve course details by its ID")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return courseService.getCourseByAbbreviation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new course", description = "Registers a new course with groups")
    @ApiResponse(responseCode = "200", description = "Course created successfully")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.createCourse(courseRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Updates course details including groups")
    public ResponseEntity<Course> updateCourse(
            @PathVariable String id, 
            @Valid @RequestBody CourseRequest courseRequest) {
        return courseService.updateCourse(id, courseRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{courseId}/groups")
    @Operation(summary = "Add group to course", description = "Adds a new group to an existing course")
    public ResponseEntity<Course> addGroupToCourse(
            @PathVariable String courseId,
            @Valid @RequestBody GroupRequest groupRequest) {
        return courseService.addGroupToCourse(courseId, groupRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course", description = "Deletes a course by its ID")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}