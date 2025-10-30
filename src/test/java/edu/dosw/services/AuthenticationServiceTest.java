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

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
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

    @Test
    void canAccessStudentData_StudentAccessingOwnData_ShouldReturnTrue() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("student@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "student123", "student@mail.escuelaing.edu.co", "password", Role.STUDENT, "");

        when(userCredentialsRepository.findByEmail("student@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessStudentData(authentication, "student123");

        assertTrue(result);
    }

    @Test
    void canAccessStudentData_StudentAccessingOtherData_ShouldReturnFalse() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("student@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "student123", "student@mail.escuelaing.edu.co", "password", Role.STUDENT, "");

        when(userCredentialsRepository.findByEmail("student@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessStudentData(authentication, "otherStudent123");

        assertFalse(result);
    }

    @Test
    void canAccessStudentData_ProfessorAccessingAnyData_ShouldReturnTrue() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("professor@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "prof123", "professor@mail.escuelaing.edu.co", "password", Role.PROFESSOR, "");

        when(userCredentialsRepository.findByEmail("professor@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessStudentData(authentication, "anyStudentId");

        assertTrue(result);
    }

    @Test
    void canAccessUserData_StudentAccessingOwnData_ShouldReturnTrue() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("student@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "student123", "student@mail.escuelaing.edu.co", "password", Role.STUDENT, "");

        when(userCredentialsRepository.findByEmail("student@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessUserData(authentication, "student123");

        assertTrue(result);
    }

    @Test
    void canAccessUserData_StudentAccessingOtherData_ShouldReturnFalse() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("student@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "student123", "student@mail.escuelaing.edu.co", "password", Role.STUDENT, "");

        when(userCredentialsRepository.findByEmail("student@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessUserData(authentication, "otherUser123");

        assertFalse(result);
    }

    @Test
    void canAccessUserData_ProfessorAccessingOwnData_ShouldReturnTrue() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("professor@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "prof123", "professor@mail.escuelaing.edu.co", "password", Role.PROFESSOR, "");

        when(userCredentialsRepository.findByEmail("professor@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessUserData(authentication, "prof123");

        assertTrue(result);
    }

    @Test
    void canAccessUserData_ProfessorAccessingOtherData_ShouldReturnFalse() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("professor@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "prof123", "professor@mail.escuelaing.edu.co", "password", Role.PROFESSOR, "");

        when(userCredentialsRepository.findByEmail("professor@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessUserData(authentication, "otherUser123");

        assertFalse(result);
    }

    @Test
    void canAccessUserData_AdminAccessingAnyData_ShouldReturnTrue() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("admin@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "admin123", "admin@mail.escuelaing.edu.co", "password", Role.ADMINISTRATOR, "");

        when(userCredentialsRepository.findByEmail("admin@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessUserData(authentication, "anyUserId");

        assertTrue(result);
    }

    @Test
    void canAccessUserRequest_Student_ShouldReturnTrue() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("student@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "student123", "student@mail.escuelaing.edu.co", "password", Role.STUDENT, "");

        when(userCredentialsRepository.findByEmail("student@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessUserRequest(authentication, "request123");

        assertTrue(result);
    }

    @Test
    void canAccessUserRequest_Professor_ShouldReturnTrue() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("professor@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "prof123", "professor@mail.escuelaing.edu.co", "password", Role.PROFESSOR, "");

        when(userCredentialsRepository.findByEmail("professor@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        boolean result = authenticationService.canAccessUserRequest(authentication, "request123");

        assertTrue(result);
    }

    @Test
    void getCurrentUserId_ShouldReturnUserId() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("user@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "user123", "user@mail.escuelaing.edu.co", "password", Role.STUDENT, "");

        when(userCredentialsRepository.findByEmail("user@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        String result = authenticationService.getCurrentUserId(authentication);

        assertEquals("user123", result);
    }

    @Test
    void getCurrentUserRole_ShouldReturnUserRole() {
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("professor@mail.escuelaing.edu.co");

        UserCredentialsDto userCredentials = new UserCredentialsDto(
                "id123", "prof123", "professor@mail.escuelaing.edu.co", "password", Role.PROFESSOR, "");

        when(userCredentialsRepository.findByEmail("professor@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(userCredentials));

        Role result = authenticationService.getCurrentUserRole(authentication);

        assertEquals(Role.PROFESSOR, result);
    }
}
