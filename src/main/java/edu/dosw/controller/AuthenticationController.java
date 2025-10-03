package edu.dosw.controller;

import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(summary = "Log in")
    public ResponseEntity<String> login(@RequestBody UserCredentialsDto userCredentialsDto) {
        Boolean isAuthenticated = authenticationService.logIn(userCredentialsDto);
        return ResponseEntity.ok(isAuthenticated.toString());
    }
}
