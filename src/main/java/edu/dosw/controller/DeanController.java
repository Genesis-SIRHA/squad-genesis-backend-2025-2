package edu.dosw.controller;

import edu.dosw.dto.DeanDto;
import edu.dosw.model.Dean;
import edu.dosw.services.DeanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/dean")
@Tag(name = "Dean controller", description = "APIs for dean management")
public class DeanController {
  private final DeanService deanService;

  @GetMapping("/{deanId}")
  @Operation(summary = "Get dean by ID", description = "Retrieves a dean by its unique identifier")
  public ResponseEntity<Dean> getDeanById(@PathVariable String deanId) {
    return ResponseEntity.ok(deanService.getDeanById(deanId));
  }

  @PostMapping("/create")
  @Operation(summary = "Create dean", description = "Creates a new dean")
  public ResponseEntity<Dean> createDean(@RequestBody DeanDto deanCreationRequest) {
    return ResponseEntity.ok(deanService.createDean(deanCreationRequest));
  }

  @PatchMapping("/update/{deanId}")
  @Operation(summary = "Update dean", description = "Updates an existing dean")
  public ResponseEntity<Dean> updateDean(
      @RequestBody DeanDto deanUpdateRequest, @PathVariable String deanId) {
    return ResponseEntity.ok(deanService.updateDean(deanId, deanUpdateRequest));
  }

  @DeleteMapping("/delete/{deanId}")
  @Operation(summary = "Delete dean", description = "Deletes an existing dean")
  public ResponseEntity<Dean> deleteDean(@PathVariable String deanId) {
    return ResponseEntity.ok(deanService.deleteDean(deanId));
  }
}
