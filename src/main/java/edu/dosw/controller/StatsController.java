package edu.dosw.controller;

import edu.dosw.dto.ReportDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.services.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/stats")
@Tag(name = "Statistics Controller", description = "APIs for generating statistical reports")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/courses/{courseAbbreviation}")
    @Operation(
            summary = "Get course reassignment statistics",
            description = "Retrieves statistics about reassignments for a specific course")
    public ResponseEntity<ReportDTO> getCourseReassignmentStats(@PathVariable String courseAbbreviation) {
        return ResponseEntity.ok(statsService.getCourseReassignmentStats(courseAbbreviation));
    }

    @GetMapping("/groups/{groupCode}")
    @Operation(
            summary = "Get group reassignment statistics",
            description = "Retrieves statistics about reassignments for a specific group")
    public ResponseEntity<ReportDTO> getGroupReassignmentStats(@PathVariable String groupCode) {
        return ResponseEntity.ok(statsService.getGroupReassignmentStats(groupCode));
    }

    @GetMapping("/faculties/{facultyName}")
    @Operation(
            summary = "Get faculty reassignment statistics",
            description = "Retrieves statistics about reassignments for a specific faculty")
    public ResponseEntity<ReportDTO> getFacultyReassignmentStats(@PathVariable String facultyName) {
        return ResponseEntity.ok(statsService.getFacultyReassignmentStats(facultyName));
    }

    @GetMapping("/global")
    @Operation(
            summary = "Get global reassignment statistics",
            description = "Retrieves comprehensive statistics about all reassignments")
    public ResponseEntity<ReportDTO> getGlobalReassignmentStats() {
        return ResponseEntity.ok(statsService.getGlobalReassignmentStats());
    }

    @GetMapping("/requests")
    @Operation(
            summary = "Get request statistics",
            description = "Retrieves basic statistics about requests")
    public ResponseEntity<RequestStats> getRequestStats() {
        return ResponseEntity.ok(statsService.getRequestStats());
    }
}