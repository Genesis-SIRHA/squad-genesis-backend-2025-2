import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Request Controller", description = "APIs for managing requests")
public class RequestController {

    private final RequestService requestService = new RequestService(new RequestRepository());


    @PostMapping
    @Operation(summary = "Create a new request", description = "Creates a new request with the provided details")
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        // En una implementación real, aquí se guardaría en una base de datos
        return ResponseEntity.ok(request);
    }

    @GetMapping
    @Operation(summary = "Get all requests", description = "Retrieves a list of all requests")
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping
    @Operation(summary = "Get requests by role", description = "Retrieves requests based on user role and ID")
    public ResponseEntity<List<Request>> fetchRequests(@RequestParam String userId, @RequestParam String role) {
        List<Request> requests = requestService.fetchRequests(role, userId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a request", description = "Updates an existing request with new details")
    public ResponseEntity<Request> updateRequest(@PathVariable Long id, @RequestBody String updatedRequest) {
        // En una implementación real, aquí se actualizaría en la base de datos
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a request", description = "Cancels a request by its ID")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        // En una implementación real, aquí se eliminaría de la base de datos
        return ResponseEntity.notFound().build();
    }
}
