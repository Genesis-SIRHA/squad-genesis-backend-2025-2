package edu.dosw.controller;

import edu.dosw.dto.ProfessorDto;
import edu.dosw.model.Professor;
import edu.dosw.services.UserServices.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/professor")
@Tag(name = "Professor controller", description = "APIs for professor management")
public class ProfessorController {
  private final ProfessorService professorService;

  @GetMapping("/{professorId}")
  @Operation(
      summary = "Get professor by ID",
      description = "Retrieves a professor by its unique identifier")
  public ResponseEntity<Professor> getProfessorById(@PathVariable String professorId) {
    return ResponseEntity.ok(professorService.getProfessorById(professorId));
  }

  @PostMapping("/create")
  @Operation(summary = "Create professor", description = "Creates a new professor")
  public ResponseEntity<Professor> createProfessor(
      @RequestBody ProfessorDto professorCreationRequest) {
    return ResponseEntity.ok(professorService.createProfessor(professorCreationRequest));
  }

  @PatchMapping("/update/{professorId}")
  @Operation(summary = "Update professor", description = "Updates an existing professor")
  public ResponseEntity<Professor> updateProfessor(
      @RequestBody ProfessorDto professorUpdateRequest, @PathVariable String professorId) {
    return ResponseEntity.ok(professorService.updateProfessor(professorId, professorUpdateRequest));
  }

  @DeleteMapping("/delete/{professorId}")
  @Operation(summary = "Delete professor", description = "Deletes an existing professor")
  public ResponseEntity<Professor> deleteProfessor(@PathVariable String professorId) {
    return ResponseEntity.ok(professorService.deleteProfessor(professorId));
  }
}
