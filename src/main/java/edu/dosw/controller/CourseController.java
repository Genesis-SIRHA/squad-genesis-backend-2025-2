package edu.dosw.controller;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.services.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
   * @param courseAbbreviation The course abbreviation
   * @param facultyName The faculty name of the faculty
   * @param plan The plan of the faculty
   * @return The course details if found, 404 otherwise
   */
  @GetMapping("/{courseAbbreviation}")
  @Operation(summary = "Get course by courseAbbreviation", description = "Retrieve course details by its courseAbbreviation")
  public ResponseEntity<Course> getCourseById(@PathVariable String courseAbbreviation, @RequestParam String facultyName, @RequestParam String plan) {
    Course course= facultyService.findCourseByAbbreviation(courseAbbreviation, facultyName, plan);
    return ResponseEntity.ok(course);
  }

  /**
   * Creates a new course.
   *
   * @param courseRequest The course details to create
   * @return The created course with its generated ID
   */
  @PostMapping
  @Operation(summary = "Create a new course", description = "Registers a new course")
  @ApiResponse(responseCode = "200", description = "Course created successfully")
  public ResponseEntity<Faculty> createCourse(@RequestBody CourseRequest courseRequest) {
    return ResponseEntity.ok(facultyService.addCourse(courseRequest));
  }

  /**
   * Updates an existing course.
   *
   * @param courseAbbreviation The courseAbbreviation of the course to update
   * @param facultyName the Faculty name of the faculty that contains the course
   * @param plan the plan that contains the course
   * @param updateCourseDTO The updated course details
   * @return The updated course if found, 404 otherwise
   */
  @PatchMapping("/{courseAbbreviation}")
  @Operation(summary = "Update course", description = "Updates course")
  public ResponseEntity<Course> updateCourse(
      @PathVariable String courseAbbreviation, @RequestParam String facultyName, @RequestParam String plan,@RequestBody UpdateCourseDTO updateCourseDTO) {
    Course course = facultyService.updateCourse(courseAbbreviation, facultyName, plan, updateCourseDTO);
    return ResponseEntity.ok((course));
  }

  /**
   * Deletes a course by its ID.
   *
   * @param courseAbbreviation The course abbreviation
   * @param facultyName The faculty name of the faculty
   * @param plan The plan of the faculty
   * @return 204 No Content if successful
   */
  @DeleteMapping("/{courseAbbreviation}")
  @Operation(summary = "Delete course by it's courseAbbreviation", description = "Deletes a course by its courseAbbreviation")
  public ResponseEntity<Void> deleteCourseByCourseAbbreviation(@PathVariable String courseAbbreviation, @RequestParam String facultyName, @RequestParam String plan) {
    facultyService.deleteCourse(courseAbbreviation ,facultyName, plan);
    return ResponseEntity.noContent().build();
  }
}
