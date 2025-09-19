package controller;

import dto.ScheduleRequest;
import dto.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.SchedulerService;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@Tag(name = "Scheduler Controller", description = "APIs for managing class schedules")
public class SchedulerController {

    private final SchedulerService schedulerService;

    @Autowired
    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping
    @Operation(summary = "Create a new schedule", description = "Creates a new class schedule with the provided details")
    public ResponseEntity<ScheduleResponse> createSchedule(@RequestBody ScheduleRequest scheduleRequest) {
        ScheduleResponse createdSchedule = schedulerService.createSchedule(scheduleRequest);
        return ResponseEntity.ok(createdSchedule);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get schedule by ID", description = "Retrieves a schedule by its unique identifier")
    public ResponseEntity<ScheduleResponse> getScheduleById(@PathVariable String id) {
        return ResponseEntity.ok(schedulerService.getScheduleById(id));
    }

    @GetMapping
    @Operation(summary = "Get all schedules", description = "Retrieves all available schedules")
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        return ResponseEntity.ok(schedulerService.getAllSchedules());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a schedule", description = "Updates an existing schedule with new information")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable String id, 
            @RequestBody ScheduleRequest scheduleRequest) {
        return ResponseEntity.ok(schedulerService.updateSchedule(id, scheduleRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a schedule", description = "Deletes a schedule by its ID")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
        schedulerService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

}
