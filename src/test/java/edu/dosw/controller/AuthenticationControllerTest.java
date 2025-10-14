package edu.dosw.controller;

import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.services.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void login_WithValidCredentials_ShouldReturnTrue() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", "user@mail.escuelaing.edu.co", "password123");
        when(authenticationService.logIn(credentials)).thenReturn(true);

        ResponseEntity<String> response = authenticationController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody());
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnFalse() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", "user@mail.escuelaing.edu.co", "wrongpassword");
        when(authenticationService.logIn(credentials)).thenReturn(false);

        ResponseEntity<String> response = authenticationController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("false", response.getBody());
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithInvalidEmailFormat_ShouldThrowException() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", "invalid-email", "password123");
        when(authenticationService.logIn(credentials)).thenThrow(new RuntimeException("Invalid email"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(credentials));
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithNonExistentUser_ShouldThrowException() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "nonExistent", "nonexistent@mail.escuelaing.edu.co", "password123");
        when(authenticationService.logIn(credentials)).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(credentials));
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithWrongPassword_ShouldThrowException() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", "user@mail.escuelaing.edu.co", "wrongpass");
        when(authenticationService.logIn(credentials)).thenThrow(new RuntimeException("Invalid password"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(credentials));
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithNullCredentials_ShouldThrowException() {
        when(authenticationService.logIn(null)).thenThrow(new RuntimeException("Credentials cannot be null"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(null));
        verify(authenticationService, times(1)).logIn(null);
    }

    @Test
    void login_WithNullEmail_ShouldThrowException() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", null, "password123");
        when(authenticationService.logIn(credentials)).thenThrow(new RuntimeException("Email cannot be null"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(credentials));
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithNullPassword_ShouldThrowException() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", "user@mail.escuelaing.edu.co", null);
        when(authenticationService.logIn(credentials)).thenThrow(new RuntimeException("Password cannot be null"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(credentials));
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithEmptyPassword_ShouldThrowException() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", "user@mail.escuelaing.edu.co", "");
        when(authenticationService.logIn(credentials)).thenThrow(new RuntimeException("Password cannot be empty"));

        assertThrows(RuntimeException.class, () -> authenticationController.login(credentials));
        verify(authenticationService, times(1)).logIn(credentials);
    }

    @Test
    void login_WithValidCredentialsDifferentCaseEmail_ShouldReturnTrue() {
        UserCredentialsDto credentials = new UserCredentialsDto("1", "user123", "USER@MAIL.ESCUELAING.EDU.CO", "password123");
        when(authenticationService.logIn(credentials)).thenReturn(true);

        ResponseEntity<String> response = authenticationController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody());
        verify(authenticationService, times(1)).logIn(credentials);
    }
}