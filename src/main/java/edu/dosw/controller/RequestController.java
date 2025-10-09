package edu.dosw.controller;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.RequestStats;
import edu.dosw.dto.UpdateRequestDto;
import edu.dosw.model.Professor;
import edu.dosw.model.Request;
import edu.dosw.model.enums.Role;
import edu.dosw.services.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles all request-related HTTP operations. Provides endpoints for creating,
 * retrieving, updating, and deleting requests, as well as retrieving request statistics.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/requests")
@Tag(name = "Request Controller", description = "APIs for managing requests")
public class RequestController {
  private final RequestService requestService;

  /**
   * Creates a new request with the provided details.
   *
   * @param request The request data to create
   * @return The created request with its generated userId and status
   */
  @PostMapping
  @Operation(
          summary = "Create a new request",
          description = "Creates a new request with the provided details")
  public ResponseEntity<Request> createRequest(@RequestBody CreateRequestDto request) {
    Request createdRequest = requestService.createRequest(request);
    return ResponseEntity.ok(createdRequest);
  }

  /**
   * Retrieves requests based on user role and userId.
   *
   * @param userId The userId of the user making the request
   * @param role The role of the user (e.g., STUDENT, PROFESSOR)
   * @return List of requests filtered by user role and userId
   */
  @GetMapping("/{role}/{userId}")
  @Operation(
      summary = "Get requests by role",
      description = "Retrieves requests based on user role and userId")
  public ResponseEntity<List<Request>> fetchRequests(
      @PathVariable String userId, @PathVariable Role role) {
    List<Request> requests = requestService.fetchRequests(role, userId);
    return ResponseEntity.ok(requests);
  }

  /**
   * Retrieves requests based on user role and userId.
   *
   * @return List of all requests
   */
  @GetMapping("/global")
  @Operation(
          summary = "Get all requests",
          description = "Retrieves all requests ")
  public ResponseEntity<List<Request>> fetchAllRequests() {
    List<Request> requests = requestService.fetchAllRequests();
    return ResponseEntity.ok(requests);
  }

  @GetMapping("/{requestId}")
  @Operation(
          summary = "Get requests by id",
          description = "Retrieves request by id ")
  public ResponseEntity<Request> getRequest(@PathVariable String requestId) {
    Request request = requestService.getRequest(requestId);
    return ResponseEntity.ok(request);
  }

  @GetMapping("/student/{studentId}")
  @Operation(
          summary = "Get historical requests by studentId",
          description = "Retrieves requests based on user role and userId")
  public ResponseEntity<List<Request>> getStudentHistorialRequests(@PathVariable String studentId) {
    List<Request> requests = requestService.fetchRequests(Role.STUDENT, studentId);
    return ResponseEntity.ok(requests);
  }


  @GetMapping("/faculty/{facultyName}")
  @Operation(
          summary = "Get faculty requests",
          description = "Retrieves requests based on faculty name")
  public ResponseEntity<List<Request>> getRequestByFacultyName(@PathVariable String facultyName) {
    List<Request> requests = requestService.fetchRequestsByFacultyName(facultyName.toLowerCase());
    return ResponseEntity.ok(requests);
  }

  /**
   * Retrieves statistics about requests.
   *
   * @return Request statistics including counts by status
   */
  @GetMapping("/stats")
  @Operation(
          summary = "Get request statistics",
          description = "Retrieves statistics about requests")
  public ResponseEntity<RequestStats> getRequestStats() {
    return ResponseEntity.ok(requestService.getRequestStats());
  }

  /**
   * Updates the status of an existing request.
   *
   * @param userId The userId of the request to update
   * @param updateRequestDto The update request dto containing the new state
   * @return The updated request
   */
  @PatchMapping("/status/{userId}")
  @Operation(
      summary = "Update request status",
      description = "Updates the status of an existing request")
  public ResponseEntity<Request> updateRequestStatus(
      @PathVariable String userId, @RequestBody UpdateRequestDto updateRequestDto) {
    return ResponseEntity.ok(requestService.updateRequest(userId, updateRequestDto));
  }

  /**
   * Cancels a request by its userId.
   *
   * @param requestId The requestId of the request to cancel
   * @return 204 No Content if successful
   */
  @DeleteMapping("/{requestId}")
  @Operation(summary = "Cancel a request", description = "Cancels a request by its requestId")
  public ResponseEntity<Request> deleteRequest(@PathVariable String requestId) {
    return ResponseEntity.ok(requestService.deleteRequestStatus(requestId));
  }

}
