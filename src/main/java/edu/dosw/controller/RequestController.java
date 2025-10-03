package edu.dosw.controller;

import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Request;
import edu.dosw.model.enums.Role;
import edu.dosw.model.enums.Status;
import edu.dosw.services.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles all request-related HTTP operations. Provides endpoints for creating,
 * retrieving, updating, and deleting requests, as well as retrieving request statistics.
 */
@RestController
@RequestMapping("/api/requests")
@Tag(name = "Request Controller", description = "APIs for managing requests")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
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
    public java.util.List<Request> fetchRequests(
            @PathVariable String userId, @PathVariable Role role) {
        return requestService.fetchRequests(role, userId);
    }

    /**
     * Creates a new request with the provided details.
     *
     * @param request The request data to create
     * @return The created request with its generated userId and status
     */
    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED) // devuelve 201
    @Operation(
            summary = "Create a new request",
            description = "Creates a new request with the provided details")
    public Request createRequest(@RequestBody RequestDTO request) {
        return requestService.createRequest(request);
    }

    /**
     * Updates the status of an existing request.
     *
     * @param userId The userId of the request to update
     * @param status The new status to set for the request
     * @return The updated request
     */
    @PutMapping("/status/{userId}")
    @Operation(
            summary = "Update request status",
            description = "Updates the status of an existing request")
    public Request updateRequestStatus(
            @PathVariable String userId, @RequestParam Status status) {
        return requestService.updateRequestStatus(userId, status);
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
    public RequestStats getRequestStats() {
        return requestService.getRequestStats();
    }

    /**
     * Cancels a request by its userId.
     *
     * @param userId The userId of the request to cancel
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) // devuelve 204 vacío
    @Operation(summary = "Cancel a request", description = "Cancels a request by its userId")
    public void deleteRequest(@PathVariable String userId) {
        requestService.updateRequestStatus(userId, Status.CANCELLED);
    }

    /**
     * Processes a response to a specific request.
     *
     * @param userId The unique identifier of the request to respond to
     * @param response The response containing the answer and status update
     * @return The updated request
     */
    @PostMapping("/{userId}/respond")
    @Operation(summary = "Respond to a request", description = "Adds a response to a request")
    public Request respondToRequest(@PathVariable String userId, @RequestBody Request response) {
        return requestService.respondToRequest(userId, response);
    }
}
