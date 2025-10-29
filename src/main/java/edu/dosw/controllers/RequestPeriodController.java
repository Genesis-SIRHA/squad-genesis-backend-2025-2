package edu.dosw.controllers;

import edu.dosw.dto.CreateRequestPeriodDTO;
import edu.dosw.dto.RequestPeriodDTO;
import edu.dosw.dto.UpdateRequestPeriodDTO;
import edu.dosw.services.RequestPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/requestPeriods")
@RequiredArgsConstructor
@Tag(name = "Request Period Management", description = "APIs for managing request periods")
public class RequestPeriodController {

  private final RequestPeriodService requestPeriodService;

  @GetMapping("/active")
  @Operation(
      summary = "Get active request period",
      description = "Retrieves the currently active request period")
  public ResponseEntity<RequestPeriodDTO> getActivePeriod() {
    return ResponseEntity.ok(requestPeriodService.getActivePeriod());
  }

  @GetMapping("/{requestPeriodId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Get request period by ID",
      description = "Retrieves a specific request period by its unique identifier")
  public ResponseEntity<RequestPeriodDTO> getPeriodById(@PathVariable String requestPeriodId) {
    return ResponseEntity.ok(requestPeriodService.getPeriodById(requestPeriodId));
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Get all request periods",
      description = "Retrieves all request periods. Requires ADMINISTRATOR role")
  public ResponseEntity<List<RequestPeriodDTO>> getAllPeriods() {
    return ResponseEntity.ok(requestPeriodService.getAllPeriods());
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Create a new request period",
      description = "Creates a new request period. Requires ADMINISTRATOR role")
  public ResponseEntity<RequestPeriodDTO> createActivePeriod(
      @RequestBody CreateRequestPeriodDTO createRequestPeriodDTO) {
    return ResponseEntity.ok(requestPeriodService.createActivePeriod(createRequestPeriodDTO));
  }

  @PutMapping("/update")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(
      summary = "Update active request period",
      description = "Updates the active request period. Requires ADMINISTRATOR role")
  public ResponseEntity<RequestPeriodDTO> updateActivePeriod(
      @RequestBody UpdateRequestPeriodDTO updateRequestPeriodDTO) {
    return ResponseEntity.ok(requestPeriodService.updateActivePeriod(updateRequestPeriodDTO));
  }
}
