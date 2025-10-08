package edu.dosw.controller;

import edu.dosw.dto.AdministratorDto;
import edu.dosw.model.Administrator;
import edu.dosw.services.AdministratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/administrator")
@Tag(name = "Administrator controller", description = "APIs for administrator management")
public class AdministratorController {
  private final AdministratorService administratorService;

  @GetMapping("/{administratorId}")
  @Operation(
      summary = "Get administrator by ID",
      description = "Retrieves a administrator by its unique identifier")
  public ResponseEntity<Administrator> getAdministratorById(@PathVariable String administratorId) {
    return ResponseEntity.ok(administratorService.getAdministratorById(administratorId));
  }

  @PostMapping("/create")
  @Operation(summary = "Create administrator", description = "Creates a new administrator")
  public ResponseEntity<Administrator> createAdministrator(
      @RequestBody AdministratorDto administratorCreationRequest) {
    return ResponseEntity.ok(
        administratorService.createAdministrator(administratorCreationRequest));
  }

  @PatchMapping("/update/{administratorId}")
  @Operation(summary = "Update administrator", description = "Updates an existing administrator")
  public ResponseEntity<Administrator> updateAdministrator(
      @RequestBody AdministratorDto administratorUpdateRequest,
      @PathVariable String administratorId) {
    return ResponseEntity.ok(
        administratorService.updateAdministrator(administratorId, administratorUpdateRequest));
  }

  @DeleteMapping("/delete/{administratorId}")
  @Operation(summary = "Delete administrator", description = "Deletes an existing administrator")
  public ResponseEntity<Administrator> deleteAdministrator(@PathVariable String administratorId) {
    return ResponseEntity.ok(administratorService.deleteAdministrator(administratorId));
  }
}
