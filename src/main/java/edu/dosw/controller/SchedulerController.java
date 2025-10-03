package edu.dosw.controller;

import edu.dosw.model.Schedule;
import edu.dosw.services.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing class schedules. Provides endpoints for accessing and managing
 * student schedules.
 */
@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Scheduler Controller", description = "APIs for managing class schedules")
public class SchedulerController {
  private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
  private final SchedulerService schedulerService;

  /**
   * Constructs a new SchedulerController with the specified SchedulerService.
   *
   * @param schedulerService The service for handling schedule-related operations
   */
  @Autowired
  public SchedulerController(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

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
    return ResponseEntity.ok(schedulerService.getScheduleById(studentId));
  }
}
