package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.dosw.dto.LogInDTO;
import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.AuthenticationException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.User;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.UserCredentialsRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock private UserCredentialsRepository userCredentialsRepository;

  @InjectMocks private AuthenticationService authenticationService;

  @Test
  void logIn_WithInvalidEmailFormat_ShouldThrowAuthenticationException() {
    UserCredentialsDto loginRequest =
        new UserCredentialsDto(
            null, "user123", "invalid@gmail.com", "password123", Role.STUDENT, "");

    AuthenticationException exception =
        assertThrows(
            AuthenticationException.class,
            () ->
                authenticationService.logIn(
                    new LogInDTO(loginRequest.email(), loginRequest.password())));

    assertEquals("Invalid email", exception.getMessage());
    verify(userCredentialsRepository, never()).findByEmail(any());
  }

  @Test
  void logIn_WithNonExistentEmail_ShouldThrowBusinessException() {
    UserCredentialsDto loginRequest =
        new UserCredentialsDto(
            null, "user123", "nonexistent@mail.escuelaing.edu.co", "password123", Role.STUDENT, "");

    when(userCredentialsRepository.findByEmail("nonexistent@mail.escuelaing.edu.co"))
        .thenReturn(Optional.empty());

    edu.dosw.exception.BusinessException exception =
        assertThrows(
            edu.dosw.exception.BusinessException.class,
            () ->
                authenticationService.logIn(
                    new LogInDTO(loginRequest.email(), loginRequest.password())));

    assertEquals("User not found", exception.getMessage());
    verify(userCredentialsRepository).findByEmail("nonexistent@mail.escuelaing.edu.co");
  }

  @Test
  void logIn_WithWrongPassword_ShouldThrowAuthenticationException() {

    UserCredentialsDto loginRequest =
        new UserCredentialsDto(
            null, "user123", "test@mail.escuelaing.edu.co", "wrongPassword", Role.STUDENT, "");
    UserCredentialsDto storedUser =
        new UserCredentialsDto(
            "id123",
            "user123",
            "test@mail.escuelaing.edu.co",
            "$2a$10$correctEncodedPassword",
            Role.STUDENT,
            "");

    when(userCredentialsRepository.findByEmail("test@mail.escuelaing.edu.co"))
        .thenReturn(Optional.of(storedUser));

    AuthenticationException exception =
        assertThrows(
            AuthenticationException.class,
            () ->
                authenticationService.logIn(
                    new LogInDTO(loginRequest.email(), loginRequest.password())));

    assertEquals("Invalid password", exception.getMessage());
    verify(userCredentialsRepository).findByEmail("test@mail.escuelaing.edu.co");
  }

  @Test
  void findByUserId_WithExistingUser_ShouldReturnUserCredentials() {

    String userId = "user123";
    UserCredentialsDto expectedCredentials =
        new UserCredentialsDto(
            "id123", userId, "test@mail.escuelaing.edu.co", "encodedPassword", Role.STUDENT, "");

    when(userCredentialsRepository.findByUserId(userId))
        .thenReturn(Optional.of(expectedCredentials));

    Optional<UserCredentialsDto> result = authenticationService.getByUserId(userId);

    assertTrue(result.isPresent());
    assertEquals(expectedCredentials, result.get());
    verify(userCredentialsRepository).findByUserId(userId);
  }

  @Test
  void findByUserId_WithNonExistentUser_ShouldThrowResourceNotFoundException() {

    String userId = "nonexistent";

    when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> authenticationService.getByUserId(userId));

    assertEquals("User not found", exception.getMessage());
    verify(userCredentialsRepository).findByUserId(userId);
  }

  @Test
  void createAuthentication_ShouldSaveUserWithEncryptedPassword() {

    User user = new User();
    user.setUserId("user123");
    user.setEmail("newuser@mail.escuelaing.edu.co");

    when(userCredentialsRepository.save(any(UserCredentialsDto.class))).thenReturn(null);

    authenticationService.createAuthentication(
        new UserInfoDto(user.getUserId(), user.getEmail(), Role.STUDENT, ""));

    verify(userCredentialsRepository).save(any(UserCredentialsDto.class));
  }

  @Test
  void deleteAuthentication_WithExistingUser_ShouldDeleteCredentials() {

    User user = new User();
    user.setEmail("test@mail.escuelaing.edu.co");
    UserCredentialsDto existingCredentials =
        new UserCredentialsDto(
            "id123", "user123", "test@mail.escuelaing.edu.co", "encodedPassword", Role.STUDENT, "");

    when(userCredentialsRepository.findByEmail("test@mail.escuelaing.edu.co"))
        .thenReturn(Optional.of(existingCredentials));

    authenticationService.deleteAuthentication(user);

    verify(userCredentialsRepository).delete(existingCredentials);
    verify(userCredentialsRepository).findByEmail("test@mail.escuelaing.edu.co");
  }

  @Test
  void deleteAuthentication_WithNonExistentUser_ShouldThrowBusinessException() {
    User user = new User();
    user.setEmail("nonexistent@mail.escuelaing.edu.co");

    when(userCredentialsRepository.findByEmail("nonexistent@mail.escuelaing.edu.co"))
        .thenReturn(Optional.empty());

    edu.dosw.exception.BusinessException exception =
        assertThrows(
            edu.dosw.exception.BusinessException.class,
            () -> authenticationService.deleteAuthentication(user));

    assertEquals("User not found", exception.getMessage());
    verify(userCredentialsRepository).findByEmail("nonexistent@mail.escuelaing.edu.co");
    verify(userCredentialsRepository, never()).delete(any());
  }
}
