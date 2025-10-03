package edu.dosw.controller;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.services.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles all course-related HTTP requests. Provides endpoints for CRUD operations
 * on courses and their groups.
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Controller", description = "APIs for managing courses and groups")
public class CourseController {

    private final FacultyService facultyService;

    public CourseController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    /**
     * Retrieves a specific course by its ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieve course details by its ID")
    public Course getCourseById(@PathVariable String id) {
        // si no existe, el servicio debería lanzar ResourceNotFoundException → lo maneja el handler
        return facultyService.findCourseByCode(id)
                .orElseThrow(() -> new edu.dosw.exception.ResourceNotFoundException("Course not found: " + id));
    }

    /**
     * Creates a new course.
     */
    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED) // 201
    @Operation(summary = "Create a new course", description = "Registers a new course with groups")
    @ApiResponse(responseCode = "201", description = "Course created successfully")
    public Faculty createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        return facultyService.createCourse(courseRequest);
    }

    /**
     * Updates an existing course.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Updates course details including groups")
    public Faculty updateCourse(
            @PathVariable String id, @Valid @RequestBody CourseRequest courseRequest) {
        return facultyService.updateCourse(id, courseRequest);
    }

    /**
     * Adds a new group to an existing course.
     */
    @PostMapping("/{courseId}/groups")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) // 204 si fue exitoso
    @Operation(summary = "Add group to course", description = "Adds a new group to an existing course")
    public void addGroupToCourse(
            @PathVariable String courseId, @Valid @RequestBody GroupRequest groupRequest) {
        Boolean result = facultyService.addGroupToCourse(groupRequest);
        if (!result) {
            throw new edu.dosw.exception.BusinessException("Could not add group to course " + courseId);
        }
    }

    /**
     * Deletes a course by its ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) // 204
    @Operation(summary = "Delete course", description = "Deletes a course by its ID")
    public void deleteCourse(@PathVariable String id) {
        facultyService.deleteCourse(id);
    }
}
