package edu.dosw.controller;

import edu.dosw.dto.AdministratorDto;
import edu.dosw.model.Administrator;
import edu.dosw.services.UserServices.AdministratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/administrator")
@Tag(name = "Administrator controller", description = "APIs for administrator management")
public class AdministratorController {
  private final AdministratorService administratorService;

  /**
   * Retrieves an administrator by their unique identifier
   *
   * @param administratorId The unique identifier of the administrator
   * @return ResponseEntity containing the administrator data
   */
  @GetMapping("/{administratorId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Get administrator by ID",
      description = "Retrieves a administrator by its unique identifier")
  public ResponseEntity<Administrator> getAdministratorById(@PathVariable String administratorId) {
    return ResponseEntity.ok(administratorService.getAdministratorById(administratorId));
  }

  /**
   * Creates a new administrator with the provided data
   *
   * @param administratorCreationRequest The DTO containing administrator creation data
   * @return ResponseEntity containing the created administrator
   */
  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Create administrator", description = "Creates a new administrator")
  public ResponseEntity<Administrator> createAdministrator(
      @RequestBody AdministratorDto administratorCreationRequest) {
    return ResponseEntity.ok(
        administratorService.createAdministrator(administratorCreationRequest));
  }

  /**
   * Updates an existing administrator with the provided data
   *
   * @param administratorUpdateRequest The DTO containing administrator update data
   * @param administratorId The unique identifier of the administrator to update
   * @return ResponseEntity containing the updated administrator
   */
  @PatchMapping("/update/{administratorId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Update administrator", description = "Updates an existing administrator")
  public ResponseEntity<Administrator> updateAdministrator(
      @RequestBody AdministratorDto administratorUpdateRequest,
      @PathVariable String administratorId) {
    return ResponseEntity.ok(
        administratorService.updateAdministrator(administratorId, administratorUpdateRequest));
  }

  /**
   * Deletes an administrator by their unique identifier
   *
   * @param administratorId The unique identifier of the administrator to delete
   * @return ResponseEntity containing the deleted administrator
   */
  @DeleteMapping("/delete/{administratorId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Delete administrator", description = "Deletes an existing administrator")
  public ResponseEntity<Administrator> deleteAdministrator(@PathVariable String administratorId) {
    return ResponseEntity.ok(administratorService.deleteAdministrator(administratorId));
  }
}
