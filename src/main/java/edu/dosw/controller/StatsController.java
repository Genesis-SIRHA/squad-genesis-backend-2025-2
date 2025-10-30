package edu.dosw.controller;

import edu.dosw.dto.ReportDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.services.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/stats")
@Tag(name = "Statistics Controller", description = "APIs for generating statistical reports")
public class StatsController {

  private final StatsService statsService;

  /**
   * Retrieves reassignment statistics for a specific course
   *
   * @param courseAbbreviation The abbreviation of the course
   * @return ResponseEntity containing course reassignment statistics
   */
  @GetMapping("/courses/{courseAbbreviation}")
  @Operation(
      summary = "Get course reassignment statistics",
      description = "Retrieves statistics about reassignments for a specific course")
  public ResponseEntity<ReportDTO> getCourseReassignmentStats(
      @PathVariable String courseAbbreviation) {
    return ResponseEntity.ok(statsService.getCourseReassignmentStats(courseAbbreviation));
  }

  /**
   * Retrieves reassignment statistics for a specific group
   *
   * @param groupCode The unique code identifying the group
   * @return ResponseEntity containing group reassignment statistics
   */
  @GetMapping("/groups/{groupCode}")
  @Operation(
      summary = "Get group reassignment statistics",
      description = "Retrieves statistics about reassignments for a specific group")
  public ResponseEntity<ReportDTO> getGroupReassignmentStats(@PathVariable String groupCode) {
    return ResponseEntity.ok(statsService.getGroupReassignmentStats(groupCode));
  }

  /**
   * Retrieves reassignment statistics for a specific faculty and academic plan
   *
   * @param facultyName The name of the faculty
   * @param plan The academic plan identifier
   * @return ResponseEntity containing faculty reassignment statistics
   */
  @GetMapping("/faculties/{facultyName}")
  @Operation(
      summary = "Get faculty reassignment statistics",
      description =
          "Retrieves statistics about reassignments for a specific faculty and academic plan")
  public ResponseEntity<ReportDTO> getFacultyReassignmentStats(
      @PathVariable String facultyName, @RequestParam String plan) {
    return ResponseEntity.ok(statsService.getFacultyReassignmentStats(facultyName, plan));
  }

  /**
   * Retrieves comprehensive reassignment statistics across the entire system
   *
   * @return ResponseEntity containing global reassignment statistics
   */
  @GetMapping("/global")
  @Operation(
      summary = "Get global reassignment statistics",
      description = "Retrieves comprehensive statistics about all reassignments")
  public ResponseEntity<ReportDTO> getGlobalReassignmentStats() {
    return ResponseEntity.ok(statsService.getGlobalReassignmentStats());
  }

  /**
   * Retrieves basic statistics about requests in the system
   *
   * @return ResponseEntity containing request statistics
   */
  @GetMapping("/requests")
  @Operation(
      summary = "Get request statistics",
      description = "Retrieves basic statistics about requests")
  public ResponseEntity<RequestStats> getRequestStats() {
    return ResponseEntity.ok(statsService.getRequestStats());
  }
}
