package edu.dosw.controller;

import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles all course-related HTTP requests. Provides endpoints for CRUD operations
 * on courses and their groups.
 */
@RestController
@RequestMapping("/pemsum")
@Tag(name = "Pemsum Controller", description = "APIs for managing courses and groups")
public class PemsumController {

  private final PemsumService pemsumService;

  /**
   * Constructs a new PemsumController with the provided FacultyService.
   *
   * @param pemsumService The service to handle course operations
   */
  public PemsumController(PemsumService pemsumService) {
    this.pemsumService = pemsumService;
  }

  /**
   * Retrieves the Pemsum of a student.
   *
   * @return List of all courses with their details
   */
  @GetMapping("/{studentId}/respond")
  @Operation(summary = "Get Pemsum", description = "Retrieves the Pemsum of a student")
  public ResponseEntity<Pemsum> getPemsum(@PathVariable String studentId) {
    return ResponseEntity.ok(pemsumService.getPemsum(studentId));
  }

  @GetMapping("/{studentId}/completed-courses")
  @Operation(
      summary = "Get completed courses percentage",
      description =
          "Retrieves the percentage of completed courses based on approved credits vs total credits in the study plan")
  public ResponseEntity<Double> getCompletedCoursesPercentage(@PathVariable String studentId) {
    return ResponseEntity.ok(pemsumService.getCompletedCoursesPercentage(studentId));
  }

  @GetMapping("/{studentId}/courses-status")
  @Operation(
      summary = "Get student courses status",
      description =
          "Retrieves a map of all courses with their status (including pending courses from faculty)")
  public ResponseEntity<Map<String, String>> getStudentCoursesStatus(
      @PathVariable String studentId) {
    return ResponseEntity.ok(pemsumService.getStudentCoursesStatus(studentId));
  }
}
