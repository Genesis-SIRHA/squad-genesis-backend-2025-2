package edu.dosw.controller;

import edu.dosw.model.Schedule;
import edu.dosw.services.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
@Tag(name = "Scheduler Controller", description = "APIs for managing class schedules")
public class SchedulerController {
  private final SchedulerService schedulerService;

  @GetMapping("/{studentId}")
  @PreAuthorize(
      "hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT') and @authenticationService.canAccessStudentData(authentication, #studentId)")
  @Operation(
      summary = "Get schedule by ID",
      description = "Retrieves a schedule by its unique identifier")
  public ResponseEntity<Schedule> getScheduleById(@PathVariable String studentId) {
    return ResponseEntity.ok(schedulerService.getScheduleById(studentId));
  }
}
