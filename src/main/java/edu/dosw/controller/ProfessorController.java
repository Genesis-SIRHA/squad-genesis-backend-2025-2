package edu.dosw.controller;

import edu.dosw.dto.ProfessorDto;
import edu.dosw.model.Professor;
import edu.dosw.services.UserServices.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/professor")
@Tag(name = "Professor controller", description = "APIs for professor management")
public class ProfessorController {
  private final ProfessorService professorService;

  /**
   * Retrieves a professor by their unique identifier
   *
   * @param professorId The unique identifier of the professor
   * @return ResponseEntity containing the professor data
   */
  @GetMapping("/{professorId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR')")
  @Operation(
      summary = "Get professor by ID",
      description = "Retrieves a professor by its unique identifier")
  public ResponseEntity<Professor> getProfessorById(@PathVariable String professorId) {
    return ResponseEntity.ok(professorService.getProfessorById(professorId));
  }

  /**
   * Creates a new professor with the provided data
   *
   * @param professorCreationRequest The DTO containing professor creation data
   * @return ResponseEntity containing the created professor
   */
  @PostMapping("/create")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Create professor", description = "Creates a new professor")
  public ResponseEntity<Professor> createProfessor(
      @RequestBody ProfessorDto professorCreationRequest) {
    return ResponseEntity.ok(professorService.createProfessor(professorCreationRequest));
  }

  /**
   * Updates an existing professor with the provided data
   *
   * @param professorUpdateRequest The DTO containing professor update data
   * @param professorId The unique identifier of the professor to update
   * @return ResponseEntity containing the updated professor
   */
  @PatchMapping("/update/{professorId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Update professor", description = "Updates an existing professor")
  public ResponseEntity<Professor> updateProfessor(
      @RequestBody ProfessorDto professorUpdateRequest, @PathVariable String professorId) {
    return ResponseEntity.ok(professorService.updateProfessor(professorId, professorUpdateRequest));
  }

  /**
   * Deletes a professor by their unique identifier
   *
   * @param professorId The unique identifier of the professor to delete
   * @return ResponseEntity containing the deleted professor
   */
  @DeleteMapping("/delete/{professorId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Delete professor", description = "Deletes an existing professor")
  public ResponseEntity<Professor> deleteProfessor(@PathVariable String professorId) {
    return ResponseEntity.ok(professorService.deleteProfessor(professorId));
  }
}
