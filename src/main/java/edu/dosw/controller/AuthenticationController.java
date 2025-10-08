package edu.dosw.controller;

import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Log in")
    public ResponseEntity<String> login(@RequestBody UserCredentialsDto userCredentialsDto) {
        String isAuthenticated = String.valueOf(authenticationService.logIn(userCredentialsDto));
        return ResponseEntity.ok(isAuthenticated);
    }
}
