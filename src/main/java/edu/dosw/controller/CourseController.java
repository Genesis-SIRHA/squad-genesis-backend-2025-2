package edu.dosw.controller;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.UpdateCourseDTO;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.services.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@Tag(name = "Course Controller", description = "APIs for managing courses and groups")
public class CourseController {

  private final FacultyService facultyService;

  /**
   * Constructs CourseController with required dependencies
   *
   * @param facultyService The faculty service to handle course operations
   */
  public CourseController(FacultyService facultyService) {
    this.facultyService = facultyService;
  }

  /**
   * Retrieves a course by its abbreviation within a specific faculty and plan
   *
   * @param courseAbbreviation The abbreviation of the course to retrieve
   * @param facultyName The name of the faculty where the course belongs
   * @param plan The academic plan identifier
   * @return ResponseEntity containing the course details
   */
  @GetMapping("/{courseAbbreviation}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT')")
  @Operation(
      summary = "Get course by courseAbbreviation",
      description = "Retrieve course details by its courseAbbreviation")
  public ResponseEntity<Course> getCourseById(
      @PathVariable String courseAbbreviation,
      @RequestParam String facultyName,
      @RequestParam String plan) {
    Course course = facultyService.findCourseByAbbreviation(courseAbbreviation, facultyName, plan);
    return ResponseEntity.ok(course);
  }

  /**
   * Creates a new course in the system
   *
   * @param courseRequest The DTO containing course creation data
   * @return ResponseEntity containing the updated faculty with the new course
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Create a new course", description = "Registers a new course")
  @ApiResponse(responseCode = "200", description = "Course created successfully")
  public ResponseEntity<Faculty> createCourse(@RequestBody CourseRequest courseRequest) {
    return ResponseEntity.ok(facultyService.addCourse(courseRequest));
  }

  /**
   * Updates an existing course with new information
   *
   * @param courseAbbreviation The abbreviation of the course to update
   * @param facultyName The name of the faculty where the course belongs
   * @param plan The academic plan identifier
   * @param updateCourseDTO The DTO containing updated course data
   * @return ResponseEntity containing the updated course
   */
  @PatchMapping("/{courseAbbreviation}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Update course", description = "Updates course")
  public ResponseEntity<Course> updateCourse(
      @PathVariable String courseAbbreviation,
      @RequestParam String facultyName,
      @RequestParam String plan,
      @RequestBody UpdateCourseDTO updateCourseDTO) {
    Course course =
        facultyService.updateCourse(courseAbbreviation, facultyName, plan, updateCourseDTO);
    return ResponseEntity.ok((course));
  }

  /**
   * Deletes a course by its abbreviation from a specific faculty and plan
   *
   * @param courseAbbreviation The abbreviation of the course to delete
   * @param facultyName The name of the faculty where the course belongs
   * @param plan The academic plan identifier
   * @return ResponseEntity with no content
   */
  @DeleteMapping("/{courseAbbreviation}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Delete course by it's courseAbbreviation",
      description = "Deletes a course by its courseAbbreviation")
  public ResponseEntity<Void> deleteCourseByCourseAbbreviation(
      @PathVariable String courseAbbreviation,
      @RequestParam String facultyName,
      @RequestParam String plan) {
    facultyService.deleteCourse(courseAbbreviation, facultyName, plan);
    return ResponseEntity.noContent().build();
  }
}
