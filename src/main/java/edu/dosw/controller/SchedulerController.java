package edu.dosw.controller;

import edu.dosw.model.Schedule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.dosw.services.SchedulerService;

@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Scheduler Controller", description = "APIs for managing class schedules")
public class SchedulerController {

    private final SchedulerService schedulerService;

    @Autowired
    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get schedule by ID", description = "Retrieves a schedule by its unique identifier")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable String studentId) {
        return ResponseEntity.ok(schedulerService.getScheduleById(studentId));
    }

}
