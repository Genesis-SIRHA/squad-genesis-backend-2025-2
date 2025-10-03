package edu.dosw.controller;

import edu.dosw.model.Schedule;
import edu.dosw.services.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing class schedules. Provides endpoints for accessing and managing
 * student schedules.
 */
@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Scheduler Controller", description = "APIs for managing class schedules")
public class SchedulerController {

  private final SchedulerService schedulerService;

  @Autowired
  public SchedulerController(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  /** Retrieves a student's schedule by their ID. */
  @GetMapping("/{studentId}")
  @Operation(
      summary = "Get schedule by ID",
      description = "Retrieves a schedule by its unique identifier")
  public Schedule getScheduleById(@PathVariable String studentId) {
    return schedulerService.getScheduleById(studentId);
  }
}
