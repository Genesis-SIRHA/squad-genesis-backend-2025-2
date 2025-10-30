package edu.dosw.controller;

import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pemsum")
@Tag(name = "Pemsum Controller", description = "APIs for managing courses and groups")
public class PemsumController {

  private final PemsumService pemsumService;

  /**
   * Constructs PemsumController with required dependencies
   *
   * @param pemsumService The pemsum service to handle student academic record operations
   */
  public PemsumController(PemsumService pemsumService) {
    this.pemsumService = pemsumService;
  }

  /**
   * Retrieves the complete Pemsum (academic record) for a specific student
   *
   * @param studentId The unique identifier of the student
   * @return ResponseEntity containing the student's Pemsum data
   */
  @GetMapping("/{studentId}/respond")
  @PreAuthorize(
      "hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT') and @authenticationService.canAccessStudentData(authentication, #studentId)")
  @Operation(summary = "Get Pemsum", description = "Retrieves the Pemsum of a student")
  public ResponseEntity<Pemsum> getPemsum(@PathVariable String studentId) {
    return ResponseEntity.ok(pemsumService.getPemsum(studentId));
  }

  /**
   * Calculates the percentage of completed courses for a student
   *
   * @param studentId The unique identifier of the student
   * @return ResponseEntity containing the percentage of completed courses
   */
  @GetMapping("/{studentId}/completed-courses")
  @Operation(
      summary = "Get completed courses percentage",
      description =
          "Retrieves the percentage of completed courses based on approved credits vs total credits in the study plan")
  public ResponseEntity<Double> getCompletedCoursesPercentage(@PathVariable String studentId) {
    return ResponseEntity.ok(pemsumService.getCompletedCoursesPercentage(studentId));
  }

  /**
   * Retrieves the status of all courses for a student including pending courses from their faculty
   *
   * @param studentId The unique identifier of the student
   * @return ResponseEntity containing a map of course names to their status
   */
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
