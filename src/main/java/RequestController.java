import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import services.RequestService;

import java.net.URI;
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
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        Request createdRequest = requestService.createRequest(request);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdRequest.getId())
            .toUri();
            
        return ResponseEntity.created(location).body(createdRequest);
    }

    @GetMapping
    @Operation(summary = "Get all requests", description = "Retrieves a list of all requests")
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requestService.getRecentRequests());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get requests by user ID", description = "Retrieves all requests for a specific user")
    public ResponseEntity<List<Request>> getRequestsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(requestService.fetchRequests("STUDENT", userId));
    }

    @GetMapping("/role")
    @Operation(summary = "Get requests by role", description = "Retrieves requests based on user role and ID")
    public ResponseEntity<List<Request>> fetchRequests(
            @RequestParam String userId, 
            @RequestParam String role) {
        List<Request> requests = requestService.fetchRequests(role, userId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update request status", description = "Updates the status of an existing request")
    public ResponseEntity<Request> updateRequestStatus(
            @PathVariable String id, 
            @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, status));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get request statistics", description = "Retrieves statistics about requests")
    public ResponseEntity<RequestService.RequestStats> getRequestStats() {
        return ResponseEntity.ok(requestService.getRequestStats());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a request", description = "Cancels a request by its ID")
    public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
        requestService.updateRequestStatus(id, "CANCELLED");
        return ResponseEntity.noContent().build();
    }
}
