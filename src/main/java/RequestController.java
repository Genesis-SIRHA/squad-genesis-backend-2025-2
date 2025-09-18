import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Request Controller", description = "APIs for managing requests")
public class RequestController {

    private final List<Request> requests = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    @PostMapping
    @Operation(summary = "Create a new request", description = "Creates a new request with the provided details")
    @ApiResponse(responseCode = "200", description = "Request created successfully")
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        request.setId(counter.incrementAndGet());
        requests.add(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping
    @Operation(summary = "Get all requests", description = "Retrieves a list of all requests")
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get request by ID", description = "Retrieves a specific request by its ID")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        return requests.stream()
                .filter(req -> req.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a request", description = "Updates an existing request with new details")
    public ResponseEntity<Request> updateRequest(@PathVariable Long id, @RequestBody Request updatedRequest) {
        return requests.stream()
                .filter(req -> req.getId().equals(id))
                .findFirst()
                .map(req -> {
                    req.setTitle(updatedRequest.getTitle());
                    req.setDescription(updatedRequest.getDescription());
                    return ResponseEntity.ok(req);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a request", description = "Cancels a request by its ID")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        boolean removed = requests.removeIf(req -> req.getId().equals(id));
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public static class Request {
        private Long id;
        private String title;
        private String description;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
