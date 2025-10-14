package edu.dosw.services;

import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.exception.AuthenticationException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.User;
import edu.dosw.repositories.UserCredentialsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @InjectMocks
    private AuthenticationService authenticationService;



    @Test
    void logIn_WithInvalidEmailFormat_ShouldThrowAuthenticationException() {
        // Given
        UserCredentialsDto loginRequest = new UserCredentialsDto(
                null, "user123", "invalid@gmail.com", "password123"
        );

        // When & Then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.logIn(loginRequest));

        assertEquals("Invalid email", exception.getMessage());
        verify(userCredentialsRepository, never()).findByEmail(any());
    }

    @Test
    void logIn_WithNonExistentEmail_ShouldThrowAuthenticationException() {
        // Given
        UserCredentialsDto loginRequest = new UserCredentialsDto(
                null, "user123", "nonexistent@mail.escuelaing.edu.co", "password123"
        );

        when(userCredentialsRepository.findByEmail("nonexistent@mail.escuelaing.edu.co"))
                .thenReturn(Optional.empty());

        // When & Then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.logIn(loginRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userCredentialsRepository).findByEmail("nonexistent@mail.escuelaing.edu.co");
    }

    @Test
    void logIn_WithWrongPassword_ShouldThrowAuthenticationException() {
        // Given
        UserCredentialsDto loginRequest = new UserCredentialsDto(
                null, "user123", "test@mail.escuelaing.edu.co", "wrongPassword"
        );
        UserCredentialsDto storedUser = new UserCredentialsDto(
                "id123", "user123", "test@mail.escuelaing.edu.co",
                "$2a$10$correctEncodedPassword" // Different from "wrongPassword"
        );

        when(userCredentialsRepository.findByEmail("test@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(storedUser));

        // When & Then
        AuthenticationException exception = assertThrows(AuthenticationException.class,
                () -> authenticationService.logIn(loginRequest));

        assertEquals("Invalid password", exception.getMessage());
        verify(userCredentialsRepository).findByEmail("test@mail.escuelaing.edu.co");
    }

    @Test
    void findByUserId_WithExistingUser_ShouldReturnUserCredentials() {
        // Given
        String userId = "user123";
        UserCredentialsDto expectedCredentials = new UserCredentialsDto(
                "id123", userId, "test@mail.escuelaing.edu.co", "encodedPassword"
        );

        when(userCredentialsRepository.findByUserId(userId))
                .thenReturn(Optional.of(expectedCredentials));

        // When
        Optional<UserCredentialsDto> result = authenticationService.findByUserId(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedCredentials, result.get());
        verify(userCredentialsRepository).findByUserId(userId);
    }

    @Test
    void findByUserId_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        // Given
        String userId = "nonexistent";

        when(userCredentialsRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authenticationService.findByUserId(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userCredentialsRepository).findByUserId(userId);
    }

    @Test
    void createAuthentication_ShouldSaveUserWithEncryptedPassword() {
        // Given
        User user = new User();
        user.setUserId("user123");
        user.setEmail("newuser@mail.escuelaing.edu.co");

        when(userCredentialsRepository.save(any(UserCredentialsDto.class)))
                .thenReturn(null);

        // When
        authenticationService.createAuthentication(user);

        // Then
        verify(userCredentialsRepository).save(any(UserCredentialsDto.class));
    }

    @Test
    void deleteAuthentication_WithExistingUser_ShouldDeleteCredentials() {
        // Given
        User user = new User();
        user.setEmail("test@mail.escuelaing.edu.co");
        UserCredentialsDto existingCredentials = new UserCredentialsDto(
                "id123", "user123", "test@mail.escuelaing.edu.co", "encodedPassword"
        );

        when(userCredentialsRepository.findByEmail("test@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(existingCredentials));

        // When
        authenticationService.deleteAuthentication(user);

        // Then
        verify(userCredentialsRepository).delete(existingCredentials);
        verify(userCredentialsRepository).findByEmail("test@mail.escuelaing.edu.co");
    }

    @Test
    void deleteAuthentication_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        // Given
        User user = new User();
        user.setEmail("nonexistent@mail.escuelaing.edu.co");

        when(userCredentialsRepository.findByEmail("nonexistent@mail.escuelaing.edu.co"))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authenticationService.deleteAuthentication(user));

        assertEquals("User not found", exception.getMessage());
        verify(userCredentialsRepository).findByEmail("nonexistent@mail.escuelaing.edu.co");
        verify(userCredentialsRepository, never()).delete(any());
    }
}