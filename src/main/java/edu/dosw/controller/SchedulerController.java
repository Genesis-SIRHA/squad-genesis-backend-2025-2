package edu.dosw.controller;

import edu.dosw.model.Schedule;
import edu.dosw.services.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing class schedules. Provides endpoints for accessing and managing
 * student schedules.
 */
@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
@Tag(name = "Scheduler Controller", description = "APIs for managing class schedules")
public class SchedulerController {
  private final SchedulerService schedulerService;

  /**
   * Retrieves a student's schedule by their ID.
   *
   * @param studentId The unique identifier of the student
   * @return ResponseEntity containing the student's schedule
   */
  @GetMapping("/{studentId}")
  @Operation(
      summary = "Get schedule by ID",
      description = "Retrieves a schedule by its unique identifier")
  public ResponseEntity<Schedule> getScheduleById(@PathVariable String studentId) {
    return ResponseEntity.ok(schedulerService.getActualScheduleByStudentId(studentId));
  }

  /**
   * Retrieves a student's schedule for a specific academic period
   *
   * @param studentId The unique identifier of the student
   * @param year The academic year
   * @param period The academic period
   * @return ResponseEntity containing the student's schedule for the specified period
   */
  @GetMapping("/{studentId}/period")
  @Operation(
      summary = "Get schedule by period",
      description = "Retrieves a student's schedule for a specific academic period")
  public ResponseEntity<Schedule> getScheduleByPeriod(
      @PathVariable String studentId, @RequestParam String year, @RequestParam String period) {
    return ResponseEntity.ok(schedulerService.getScheduleByPeriod(studentId, year, period));
  }

  /**
   * Retrieves all past schedules for a student
   *
   * @param studentId The unique identifier of the student
   * @return ResponseEntity containing list of past schedules
   */
  @GetMapping("/{studentId}/past")
  @Operation(
      summary = "Get past schedules",
      description = "Retrieves all past semester schedules for a student")
  public ResponseEntity<List<Schedule>> getPastSchedules(@PathVariable String studentId) {
    return ResponseEntity.ok(schedulerService.getPastSchedules(studentId));
  }
}
