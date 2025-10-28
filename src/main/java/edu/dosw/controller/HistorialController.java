package edu.dosw.controller;

import edu.dosw.model.Historial;
import edu.dosw.services.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/historial")
@Tag(name = "historial Controller", description = "APIs for historial management")
public class HistorialController {
  HistorialService historialService;

  @GetMapping("/global")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(description = "Get all historial")
  public ResponseEntity<List<Historial>> getAllHistorial() {
    return ResponseEntity.ok(historialService.getAllHistorial());
  }
}
