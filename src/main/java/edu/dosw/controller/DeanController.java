package edu.dosw.controller;

import edu.dosw.dto.DeanDto;
import edu.dosw.model.Dean;
import edu.dosw.services.UserServices.DeanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/dean")
@Tag(name = "Dean controller", description = "APIs for dean management")
public class DeanController {
  private final DeanService deanService;

  /**
   * Retrieves a dean by their unique identifier
   *
   * @param deanId The unique identifier of the dean
   * @return ResponseEntity containing the dean data
   */
  @GetMapping("/{deanId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Get dean by ID", description = "Retrieves a dean by its unique identifier")
  public ResponseEntity<Dean> getDeanById(@PathVariable String deanId) {
    return ResponseEntity.ok(deanService.getDeanById(deanId));
  }

  /**
   * Creates a new dean with the provided data
   *
   * @param deanCreationRequest The DTO containing dean creation data
   * @return ResponseEntity containing the created dean
   */
  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Create dean", description = "Creates a new dean")
  public ResponseEntity<Dean> createDean(@RequestBody DeanDto deanCreationRequest) {
    return ResponseEntity.ok(deanService.createDean(deanCreationRequest));
  }

  /**
   * Updates an existing dean with the provided data
   *
   * @param deanUpdateRequest The DTO containing dean update data
   * @param deanId The unique identifier of the dean to update
   * @return ResponseEntity containing the updated dean
   */
  @PatchMapping("/update/{deanId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Update dean", description = "Updates an existing dean")
  public ResponseEntity<Dean> updateDean(
      @RequestBody DeanDto deanUpdateRequest, @PathVariable String deanId) {
    return ResponseEntity.ok(deanService.updateDean(deanId, deanUpdateRequest));
  }

  /**
   * Deletes a dean by their unique identifier
   *
   * @param deanId The unique identifier of the dean to delete
   * @return ResponseEntity containing the deleted dean
   */
  @DeleteMapping("/delete/{deanId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Delete dean", description = "Deletes an existing dean")
  public ResponseEntity<Dean> deleteDean(@PathVariable String deanId) {
    return ResponseEntity.ok(deanService.deleteDean(deanId));
  }
}
