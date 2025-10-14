package edu.dosw.controller;

import edu.dosw.dto.AdministratorDto;
import edu.dosw.model.Administrator;
import edu.dosw.services.UserServices.AdministratorService;
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
class AdministratorControllerTest {

    @Mock
    private AdministratorService administratorService;

    @InjectMocks
    private AdministratorController administratorController;

    @Test
    void getAdministratorById_WhenAdministratorExists_ShouldReturnAdministrator() {
        String administratorId = "admin123";
        Administrator expectedAdmin = new Administrator.AdministratorBuilder()
                .userId(administratorId)
                .fullName("John Doe")
                .email("john.doe@admin.escuelaing.edu.co")
                .identityDocument("123456789")
                .build();

        when(administratorService.getAdministratorById(administratorId)).thenReturn(expectedAdmin);

        ResponseEntity<Administrator> response = administratorController.getAdministratorById(administratorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(administratorId, response.getBody().getUserId());
        verify(administratorService, times(1)).getAdministratorById(administratorId);
    }

    @Test
    void getAdministratorById_WhenAdministratorNotExists_ShouldThrowException() {
        String administratorId = "nonExistentAdmin";
        when(administratorService.getAdministratorById(administratorId))
                .thenThrow(new RuntimeException("Administrator not found"));

        assertThrows(RuntimeException.class, () ->
                administratorController.getAdministratorById(administratorId));
        verify(administratorService, times(1)).getAdministratorById(administratorId);
    }

    @Test
    void createAdministrator_WithValidData_ShouldReturnCreatedAdministrator() {
        AdministratorDto creationRequest = new AdministratorDto("John Doe", "123456789");

        Administrator createdAdmin = new Administrator.AdministratorBuilder()
                .userId("generatedId123")
                .fullName("John Doe")
                .email("john.doe-a@admin.escuelaing.edu.co")
                .identityDocument("123456789")
                .build();

        when(administratorService.createAdministrator(creationRequest)).thenReturn(createdAdmin);

        ResponseEntity<Administrator> response = administratorController.createAdministrator(creationRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("generatedId123", response.getBody().getUserId());
        verify(administratorService, times(1)).createAdministrator(creationRequest);
    }

    @Test
    void createAdministrator_WithInvalidData_ShouldThrowException() {
        AdministratorDto invalidRequest = new AdministratorDto(null, null);
        when(administratorService.createAdministrator(invalidRequest))
                .thenThrow(new RuntimeException("Personal data is incomplete"));

        assertThrows(RuntimeException.class, () ->
                administratorController.createAdministrator(invalidRequest));
        verify(administratorService, times(1)).createAdministrator(invalidRequest);
    }

    @Test
    void updateAdministrator_WithValidData_ShouldReturnUpdatedAdministrator() {
        String administratorId = "admin123";
        AdministratorDto updateRequest = new AdministratorDto("Jane Doe Updated", "987654321");

        Administrator updatedAdmin = new Administrator.AdministratorBuilder()
                .userId(administratorId)
                .fullName("Jane Doe Updated")
                .email("jane.doe-u@admin.escuelaing.edu.co")
                .identityDocument("987654321")
                .build();

        when(administratorService.updateAdministrator(administratorId, updateRequest)).thenReturn(updatedAdmin);

        ResponseEntity<Administrator> response = administratorController.updateAdministrator(updateRequest, administratorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(administratorId, response.getBody().getUserId());
        verify(administratorService, times(1)).updateAdministrator(administratorId, updateRequest);
    }

    @Test
    void updateAdministrator_WhenAdministratorNotExists_ShouldThrowException() {
        String administratorId = "nonExistentAdmin";
        AdministratorDto updateRequest = new AdministratorDto("Jane Doe", "987654321");
        when(administratorService.updateAdministrator(administratorId, updateRequest))
                .thenThrow(new RuntimeException("Administrator not found"));

        assertThrows(RuntimeException.class, () ->
                administratorController.updateAdministrator(updateRequest, administratorId));
        verify(administratorService, times(1)).updateAdministrator(administratorId, updateRequest);
    }

    @Test
    void deleteAdministrator_WhenAdministratorExists_ShouldReturnDeletedAdministrator() {
        String administratorId = "admin123";
        Administrator deletedAdmin = new Administrator.AdministratorBuilder()
                .userId(administratorId)
                .fullName("John Doe")
                .email("john.doe@admin.escuelaing.edu.co")
                .identityDocument("123456789")
                .build();

        when(administratorService.deleteAdministrator(administratorId)).thenReturn(deletedAdmin);

        ResponseEntity<Administrator> response = administratorController.deleteAdministrator(administratorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(administratorId, response.getBody().getUserId());
        verify(administratorService, times(1)).deleteAdministrator(administratorId);
    }

    @Test
    void deleteAdministrator_WhenAdministratorNotExists_ShouldThrowException() {
        String administratorId = "nonExistentAdmin";
        when(administratorService.deleteAdministrator(administratorId))
                .thenThrow(new RuntimeException("Administrator not found"));

        assertThrows(RuntimeException.class, () ->
                administratorController.deleteAdministrator(administratorId));
        verify(administratorService, times(1)).deleteAdministrator(administratorId);
    }

    @Test
    void createAdministrator_WithDuplicateIdentityDocument_ShouldThrowException() {
        AdministratorDto duplicateRequest = new AdministratorDto("John Doe", "existingDocument");

        when(administratorService.createAdministrator(duplicateRequest))
                .thenThrow(new RuntimeException("Identity document already exists"));

        assertThrows(RuntimeException.class, () ->
                administratorController.createAdministrator(duplicateRequest));
        verify(administratorService, times(1)).createAdministrator(duplicateRequest);
    }

    @Test
    void updateAdministrator_WithDuplicateIdentityDocument_ShouldThrowException() {
        String administratorId = "admin123";
        AdministratorDto updateRequest = new AdministratorDto("John Doe", "existingDocument");

        when(administratorService.updateAdministrator(administratorId, updateRequest))
                .thenThrow(new RuntimeException("Identity document already exists"));

        assertThrows(RuntimeException.class, () ->
                administratorController.updateAdministrator(updateRequest, administratorId));
        verify(administratorService, times(1)).updateAdministrator(administratorId, updateRequest);
    }
}