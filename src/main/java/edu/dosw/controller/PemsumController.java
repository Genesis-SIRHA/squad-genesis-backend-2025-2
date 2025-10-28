package edu.dosw.controller;

import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pemsum")
@Tag(name = "Pemsum Controller", description = "APIs for managing courses and groups")
public class PemsumController {

  private final PemsumService pemsumService;

  public PemsumController(PemsumService pemsumService) {
    this.pemsumService = pemsumService;
  }

  @GetMapping("/{studentId}/respond")
  @PreAuthorize(
      "hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT') and @authenticationService.canAccessStudentData(authentication, #studentId)")
  @Operation(summary = "Get Pemsum", description = "Retrieves the Pemsum of a student")
  public ResponseEntity<Pemsum> getPemsum(@PathVariable String studentId) {
    return ResponseEntity.ok(pemsumService.getPemsum(studentId));
  }
}
