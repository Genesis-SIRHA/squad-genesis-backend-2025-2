package edu.dosw.controller;

import edu.dosw.dto.AuthResponseDto;
import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<AuthResponseDto> login(
      @RequestBody @Valid UserCredentialsDto userCredentialsDto) {
    AuthResponseDto response = authenticationService.logIn(userCredentialsDto);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  @Operation(
      summary = "Get current user info",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<UserInfoDto> getCurrentUser(Authentication authentication) {
    String email = authentication.getName();
    UserInfoDto userInfo = authenticationService.getUserInfo(email);
    return ResponseEntity.ok(userInfo);
  }
}
