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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles all course-related HTTP requests. Provides endpoints for CRUD operations
 * on courses and their groups.
 */
@RestController
@RequestMapping("/courses")
@Tag(name = "Course Controller", description = "APIs for managing courses and groups")
public class CourseController {

  private final FacultyService facultyService;

  /**
   * Constructs a new CourseController with the provided FacultyService.
   *
   * @param facultyService The service to handle course operations
   */
  public CourseController(FacultyService facultyService) {
    this.facultyService = facultyService;
  }

  /**
   * Retrieves a specific course by its ID.
   *
   * @param id The ID of the course to retrieve
   * @return The course details if found, 404 otherwise
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get course by ID", description = "Retrieve course details by its ID")
  public ResponseEntity<Course> getCourseById(@PathVariable String id) {
    return facultyService
        .findCourseByCode(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Creates a new course.
   *
   * @param courseRequest The course details to create
   * @return The created course with its generated ID
   */
  @PostMapping
  @Operation(summary = "Create a new course", description = "Registers a new course with groups")
  @ApiResponse(responseCode = "200", description = "Course created successfully")
  public ResponseEntity<Faculty> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
    return ResponseEntity.ok(facultyService.createCourse(courseRequest));
  }

  /**
   * Updates an existing course.
   *
   * @param abbreviation The ID of the course to update
   * @param courseRequest The updated course details
   * @return The updated course if found, 404 otherwise
   */
  @PatchMapping("/{abbreviation}")
  @Operation(summary = "Update course", description = "Updates course")
  public ResponseEntity<Course> updateCourse(
      @PathVariable String abbreviation, @Valid @RequestBody CourseRequest courseRequest) {
    Course course = facultyService.updateCourse(abbreviation, courseRequest);
    return ResponseEntity.ok((course));
  }

  /**
   * Adds a new group to an existing course.
   *
   * @param courseId The ID of the course to add the group to
   * @param groupRequest The details of the group to add
   * @return The updated course with the new group if found, 404 otherwise
   */
  @PostMapping("/{courseId}/groups")
  @Operation(
      summary = "Add group to course",
      description = "Adds a new group to an existing course")
  public ResponseEntity<Course> addGroupToCourse(
      @PathVariable String courseId, @Valid @RequestBody GroupRequest groupRequest) {
    Boolean result = facultyService.addGroupToCourse(groupRequest);
    if (result) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  /**
   * Deletes a course by its ID.
   *
   * @param id The ID of the course to delete
   * @return 204 No Content if successful
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete course", description = "Deletes a course by its ID")
  public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
    facultyService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }
}
