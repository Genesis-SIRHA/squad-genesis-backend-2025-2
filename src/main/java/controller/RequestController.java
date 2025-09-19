package controller;

import dto.RequestDTO;
import dto.RequestResponse;
import dto.RequestStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.RequestService;

import java.util.List;
import model.Request;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Request Controller", description = "APIs for managing requests")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @Operation(summary = "Create a new request", description = "Creates a new request with the provided details")
    public ResponseEntity<RequestResponse> createRequest(@RequestBody RequestDTO request) {
        RequestResponse createdRequest = requestService.createRequest(request);
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping("/{id}/role")
    @Operation(summary = "Get requests by role", description = "Retrieves requests based on user role and ID")
    public ResponseEntity<List<Request>> fetchRequests(@PathVariable String userId, @RequestParam String role) {
        List<Request> requests = requestService.fetchRequests(role, userId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update request status", description = "Updates the status of an existing request")
    public ResponseEntity<Request> updateRequestStatus(@PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, status));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get request statistics", description = "Retrieves statistics about requests")
    public ResponseEntity<RequestStats> getRequestStats() {
        return ResponseEntity.ok(requestService.getRequestStats());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a request", description = "Cancels a request by its ID")
    public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
        requestService.updateRequestStatus(id, "CANCELLED");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/respond")
    @Operation(summary = "Respond to a request", description = "Adds a response to a request")
    public ResponseEntity<Request> respondToRequest(@PathVariable String id, @RequestBody RequestDetails response) {
        Request request = requestService.respondToRequest(id, response);
        if (request != null) {
            return ResponseEntity.ok(request);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
