package edu.dosw.controller;

import edu.dosw.dto.FacultyDto;
import edu.dosw.model.Faculty;
import edu.dosw.services.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/faculty")
@Tag(name = "Faculty Controller", description = "APIs for faculty management")
public class FacultyController {
  private FacultyService facultyService;

  @PostMapping("/create")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Create faculty")
  public ResponseEntity<Faculty> createFaculty(@RequestBody FacultyDto facultyDto) {
    return ResponseEntity.ok(facultyService.createFaculty(facultyDto));
  }

  @GetMapping("/{facultyName}/{plan}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT')")
  @Operation(summary = "Get faculty by name and plan")
  public ResponseEntity<Faculty> getFacultyByNameAndPlan(
      @PathVariable String facultyName, @PathVariable String plan) {
    return ResponseEntity.ok(facultyService.getFacultyByNameAndPlan(facultyName, plan));
  }

  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT')")
  @Operation(summary = "Get all faculties")
  public ResponseEntity<List<Faculty>> getAllFaculties() {
    return ResponseEntity.ok(facultyService.getAllFaculties());
  }

  @PatchMapping("/update")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Update faculty by name and plan")
  public ResponseEntity<Faculty> updateFacultyByNameAndPlan(@RequestBody FacultyDto facultyDto) {
    return ResponseEntity.ok(facultyService.updateFacultyByNameAndPlan(facultyDto));
  }
}
