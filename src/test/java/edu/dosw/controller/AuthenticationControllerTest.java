package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.dosw.dto.AuthResponseDto;
import edu.dosw.dto.LogInDTO;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.model.enums.Role;
import edu.dosw.services.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  @Mock private AuthenticationService authenticationService;

  @InjectMocks private AuthenticationController authenticationController;

  @Test
  void login_WithValidCredentials_ShouldReturnTokenAndUserInfo() {
    // Arrange
    LogInDTO loginDto = new LogInDTO("user@mail.escuelaing.edu.co", "password123");
    UserInfoDto userInfo = new UserInfoDto("1", "user@mail.escuelaing.edu.co", Role.STUDENT,"");
    AuthResponseDto expectedResponse = new AuthResponseDto("testToken123", userInfo);

    when(authenticationService.logIn(any(LogInDTO.class))).thenReturn(expectedResponse);

    // Act
    ResponseEntity<AuthResponseDto> response = authenticationController.login(loginDto);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("testToken123", response.getBody().token());
    assertEquals("1", response.getBody().user().userId());
    assertEquals("user@mail.escuelaing.edu.co", response.getBody().user().email());
    assertEquals(Role.STUDENT, response.getBody().user().role());
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }

  @Test
  void login_WithInvalidEmailFormat_ShouldThrowException() {
    // Arrange
    LogInDTO loginDto = new LogInDTO("invalid-email", "password123");
    when(authenticationService.logIn(any(LogInDTO.class)))
        .thenThrow(new RuntimeException("Invalid email"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> authenticationController.login(loginDto));
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }

  @Test
  void login_WithNonExistentUser_ShouldThrowException() {
    // Arrange
    LogInDTO loginDto = new LogInDTO("nonexistent@mail.escuelaing.edu.co", "password123");
    when(authenticationService.logIn(any(LogInDTO.class)))
        .thenThrow(new RuntimeException("User not found"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> authenticationController.login(loginDto));
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }

  @Test
  void login_WithWrongPassword_ShouldThrowException() {
    // Arrange
    LogInDTO loginDto = new LogInDTO("user@mail.escuelaing.edu.co", "wrongpass");
    when(authenticationService.logIn(any(LogInDTO.class)))
        .thenThrow(new RuntimeException("Invalid password"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> authenticationController.login(loginDto));
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }

  @Test
  void login_WithNullEmail_ShouldThrowException() {
    // Arrange
    LogInDTO loginDto = new LogInDTO(null, "password123");
    when(authenticationService.logIn(any(LogInDTO.class)))
        .thenThrow(new RuntimeException("Email cannot be null"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> authenticationController.login(loginDto));
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }

  @Test
  void login_WithNullPassword_ShouldThrowException() {
    // Arrange
    LogInDTO loginDto = new LogInDTO("user@mail.escuelaing.edu.co", null);
    when(authenticationService.logIn(any(LogInDTO.class)))
        .thenThrow(new RuntimeException("Password cannot be null"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> authenticationController.login(loginDto));
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }

  @Test
  void login_WithEmptyPassword_ShouldThrowException() {
    // Arrange
    LogInDTO loginDto = new LogInDTO("user@mail.escuelaing.edu.co", "");
    when(authenticationService.logIn(any(LogInDTO.class)))
        .thenThrow(new RuntimeException("Password cannot be empty"));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> authenticationController.login(loginDto));
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }

  @Test
  void login_WithValidCredentialsDifferentCaseEmail_ShouldReturnToken() {
    // Arrange
    LogInDTO loginDto = new LogInDTO("USER@MAIL.ESCUELAING.EDU.CO", "password123");
    UserInfoDto userInfo = new UserInfoDto("1", "user@mail.escuelaing.edu.co", Role.STUDENT,"");
    AuthResponseDto expectedResponse = new AuthResponseDto("testToken123", userInfo);

    when(authenticationService.logIn(any(LogInDTO.class))).thenReturn(expectedResponse);

    // Act
    ResponseEntity<AuthResponseDto> response = authenticationController.login(loginDto);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("testToken123", response.getBody().token());
    assertEquals("1", response.getBody().user().userId());
    assertEquals("user@mail.escuelaing.edu.co", response.getBody().user().email());
    assertEquals(Role.STUDENT, response.getBody().user().role());
    verify(authenticationService, times(1)).logIn(any(LogInDTO.class));
  }
}
