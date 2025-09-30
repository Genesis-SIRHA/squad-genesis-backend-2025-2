package edu.dosw.controller;

import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.dosw.services.RequestService;

import java.util.List;
import edu.dosw.model.Request;

/**
 * Controller that handles all request-related HTTP operations.
 * Provides endpoints for creating, retrieving, updating, and deleting requests,
 * as well as retrieving request statistics.
 */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "Request Controller", description = "APIs for managing requests")
public class RequestController {


    private final RequestService requestService;

    /**
     * Creates a new request with the provided details.
     *
     * @param request The request data to create
     * @return The created request with its generated ID and status
     */
    @PostMapping
    @Operation(summary = "Create a new request", description = "Creates a new request with the provided details")
    public ResponseEntity<Request> createRequest(@RequestBody RequestDTO request) {
        Request createdRequest = requestService.createRequest(request);
        return ResponseEntity.ok(createdRequest);
    }

    /**
     * Retrieves requests based on user role and ID.
     *
     * @param userId The ID of the user making the request
     * @param role   The role of the user (e.g., STUDENT, PROFESSOR)
     * @return List of requests filtered by user role and ID
     */
    @GetMapping("/{userId}/role")
    @Operation(summary = "Get requests by role", description = "Retrieves requests based on user role and ID")
    public ResponseEntity<List<Request>> fetchRequests(
            @PathVariable String userId,
            @RequestParam String role
    ) {
        List<Request> requests = requestService.fetchRequests(role, userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Updates the status of an existing request.
     *
     * @param id     The ID of the request to update
     * @param status The new status to set for the request
     * @return The updated request
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update request status", description = "Updates the status of an existing request")
    public ResponseEntity<Request> updateRequestStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, status));
    }

    /**
     * Retrieves statistics about requests.
     *
     * @return Request statistics including counts by status
     */
    @GetMapping("/stats")
    @Operation(summary = "Get request statistics", description = "Retrieves statistics about requests")
    public ResponseEntity<RequestStats> getRequestStats() {
        return ResponseEntity.ok(requestService.getRequestStats());
    }

    /**
     * Cancels a request by its ID.
     *
     * @param id The ID of the request to cancel
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a request", description = "Cancels a request by its ID")
    public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
        requestService.updateRequestStatus(id, "CANCELLED");
        return ResponseEntity.noContent().build();
    }

    /**
     * Processes a response to a specific request.
     *
     * @param id The unique identifier of the request to respond to
     * @param response The response containing the answer and status update
     * @return ResponseEntity containing the updated request if found, or null if not found
     */
    @PostMapping("/{id}/respond")
    @Operation(summary = "Respond to a request", description = "Adds a response to a request")
    public ResponseEntity<Request> respondToRequest(@PathVariable String id, @RequestBody Request response) {
        Request request = requestService.respondToRequest(id, response);
        if (request != null) {
            return ResponseEntity.ok(request);
        }
        return null;
    }
}
