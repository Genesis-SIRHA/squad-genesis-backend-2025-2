package edu.dosw.controller;

import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles all course-related HTTP requests. Provides endpoints for CRUD operations
 * on courses and their groups.
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "Pemsum Controller", description = "APIs for managing courses and groups")
public class PemsumController {

    private final PemsumService pemsumService;

    public PemsumController(PemsumService pemsumService) {
        this.pemsumService = pemsumService;
    }

    /**
     * Retrieves the Pemsum of a student.
     *
     * @return Pemsum details
     */
    @GetMapping("/{studentId}/respond")
    @Operation(summary = "Get Pemsum", description = "Retrieves the Pemsum of a student")
    public Pemsum getPemsum(@PathVariable String studentId) {
        return pemsumService.getPemsum(studentId);
    }
}
