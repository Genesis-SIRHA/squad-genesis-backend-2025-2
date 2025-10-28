package edu.dosw.controller;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.UpdateRequestDto;
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

  @PostMapping
  @Operation(
      summary = "Create a new request",
      description = "Creates a new request with the provided details")
  public ResponseEntity<Request> createRequest(@RequestBody CreateRequestDto request) {
    Request createdRequest = requestService.createRequest(request);
    return ResponseEntity.ok(createdRequest);
  }

  @GetMapping("/{role}/{userId}")
  @Operation(
      summary = "Get requests by role",
      description = "Retrieves requests based on user role and userId")
  public ResponseEntity<List<Request>> fetchRequests(
      @PathVariable String userId, @PathVariable Role role) {
    List<Request> requests = requestService.fetchRequests(role, userId);
    return ResponseEntity.ok(requests);
  }

  @GetMapping("/waitingList/{groupCode}")
  @Operation(
      summary = "Get waiting list of a group",
      description = "Retrieves a list of student IDs in the waiting list for the specified group")
  public ResponseEntity<List<String>> getWaitingListOfGroup(@PathVariable String groupCode) {
    List<String> waitingList = requestService.getWaitingListOfGroup(groupCode);
    return ResponseEntity.ok(waitingList);
  }

  @GetMapping("/global")
  @Operation(summary = "Get all requests", description = "Retrieves all requests ")
  public ResponseEntity<List<Request>> fetchAllRequests() {
    List<Request> requests = requestService.fetchAllRequests();
    return ResponseEntity.ok(requests);
  }

  @GetMapping("/{requestId}")
  @Operation(summary = "Get requests by id", description = "Retrieves request by id ")
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

  @PatchMapping("/status/{userId}")
  @Operation(
      summary = "Update request status",
      description = "Updates the status of an existing request")
  public ResponseEntity<Request> updateRequestStatus(
      @PathVariable String userId, @RequestBody UpdateRequestDto updateRequestDto) {
    return ResponseEntity.ok(requestService.updateRequest(userId, updateRequestDto));
  }

  @DeleteMapping("/{requestId}")
  @Operation(summary = "Cancel a request", description = "Cancels a request by its requestId")
  public ResponseEntity<Request> deleteRequest(@PathVariable String requestId) {
    return ResponseEntity.ok(requestService.deleteRequestStatus(requestId));
  }
}
