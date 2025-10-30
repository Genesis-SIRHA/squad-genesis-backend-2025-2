package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.DeanDto;
import edu.dosw.model.Dean;
import edu.dosw.services.UserServices.DeanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DeanControllerTest {

  @Mock private DeanService deanService;

  @InjectMocks private DeanController deanController;

  @Test
  void getDeanById_WhenDeanExists_ShouldReturnDean() {
    String deanId = "dean123";
    Dean expectedDean =
        new Dean.DeanBuilder()
            .userId(deanId)
            .fullName("John Doe")
            .email("john.doe@escuelaing.edu.co")
            .identityDocument("123456789")
            .facultyName("Engineering")
            .build();

    when(deanService.getDeanById(deanId)).thenReturn(expectedDean);

    ResponseEntity<Dean> response = deanController.getDeanById(deanId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(deanId, response.getBody().getUserId());
    verify(deanService, times(1)).getDeanById(deanId);
  }

  @Test
  void getDeanById_WhenDeanNotExists_ShouldThrowException() {
    String deanId = "nonExistentDean";
    when(deanService.getDeanById(deanId)).thenThrow(new RuntimeException("Dean not found"));

    assertThrows(RuntimeException.class, () -> deanController.getDeanById(deanId));
    verify(deanService, times(1)).getDeanById(deanId);
  }

  @Test
  void createDean_WithValidData_ShouldReturnCreatedDean() {
    DeanDto creationRequest = new DeanDto("John Doe", "123456789", "Engineering");

    Dean createdDean =
        new Dean.DeanBuilder()
            .userId("generatedId123")
            .fullName("John Doe")
            .email("john.doe-a@escuelaing.edu.co")
            .identityDocument("123456789")
            .facultyName("Engineering")
            .build();

    when(deanService.createDean(creationRequest)).thenReturn(createdDean);

    ResponseEntity<Dean> response = deanController.createDean(creationRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("generatedId123", response.getBody().getUserId());
    verify(deanService, times(1)).createDean(creationRequest);
  }

  @Test
  void createDean_WithInvalidData_ShouldThrowException() {
    DeanDto invalidRequest = new DeanDto(null, null, null);
    when(deanService.createDean(invalidRequest))
        .thenThrow(new RuntimeException("Personal data is incomplete"));

    assertThrows(RuntimeException.class, () -> deanController.createDean(invalidRequest));
    verify(deanService, times(1)).createDean(invalidRequest);
  }

  @Test
  void updateDean_WithValidData_ShouldReturnUpdatedDean() {
    String deanId = "dean123";
    DeanDto updateRequest = new DeanDto("Jane Doe Updated", "987654321", "Science");

    Dean updatedDean =
        new Dean.DeanBuilder()
            .userId(deanId)
            .fullName("Jane Doe Updated")
            .email("jane.doe-u@escuelaing.edu.co")
            .identityDocument("987654321")
            .facultyName("Science")
            .build();

    when(deanService.updateDean(deanId, updateRequest)).thenReturn(updatedDean);

    ResponseEntity<Dean> response = deanController.updateDean(updateRequest, deanId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(deanId, response.getBody().getUserId());
    verify(deanService, times(1)).updateDean(deanId, updateRequest);
  }

  @Test
  void updateDean_WhenDeanNotExists_ShouldThrowException() {
    String deanId = "nonExistentDean";
    DeanDto updateRequest = new DeanDto("Jane Doe", "987654321", "Science");
    when(deanService.updateDean(deanId, updateRequest))
        .thenThrow(new RuntimeException("Dean not found"));

    assertThrows(RuntimeException.class, () -> deanController.updateDean(updateRequest, deanId));
    verify(deanService, times(1)).updateDean(deanId, updateRequest);
  }

  @Test
  void deleteDean_WhenDeanExists_ShouldReturnDeletedDean() {
    String deanId = "dean123";
    Dean deletedDean =
        new Dean.DeanBuilder()
            .userId(deanId)
            .fullName("John Doe")
            .email("john.doe@escuelaing.edu.co")
            .identityDocument("123456789")
            .facultyName("Engineering")
            .build();

    when(deanService.deleteDean(deanId)).thenReturn(deletedDean);

    ResponseEntity<Dean> response = deanController.deleteDean(deanId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(deanId, response.getBody().getUserId());
    verify(deanService, times(1)).deleteDean(deanId);
  }

  @Test
  void deleteDean_WhenDeanNotExists_ShouldThrowException() {
    String deanId = "nonExistentDean";
    when(deanService.deleteDean(deanId)).thenThrow(new RuntimeException("Dean not found"));

    assertThrows(RuntimeException.class, () -> deanController.deleteDean(deanId));
    verify(deanService, times(1)).deleteDean(deanId);
  }

  @Test
  void createDean_WithDuplicateIdentityDocument_ShouldThrowException() {
    DeanDto duplicateRequest = new DeanDto("John Doe", "existingDocument", "Engineering");

    when(deanService.createDean(duplicateRequest))
        .thenThrow(new RuntimeException("Identity document already exists"));

    assertThrows(RuntimeException.class, () -> deanController.createDean(duplicateRequest));
    verify(deanService, times(1)).createDean(duplicateRequest);
  }

  @Test
  void updateDean_WithDuplicateIdentityDocument_ShouldThrowException() {
    String deanId = "dean123";
    DeanDto updateRequest = new DeanDto("John Doe", "existingDocument", "Engineering");

    when(deanService.updateDean(deanId, updateRequest))
        .thenThrow(new RuntimeException("Identity document already exists"));

    assertThrows(RuntimeException.class, () -> deanController.updateDean(updateRequest, deanId));
    verify(deanService, times(1)).updateDean(deanId, updateRequest);
  }

  @Test
  void createDean_WithInvalidFaculty_ShouldThrowException() {
    DeanDto invalidRequest = new DeanDto("John Doe", "123456789", "NonExistentFaculty");

    when(deanService.createDean(invalidRequest))
        .thenThrow(new RuntimeException("Faculty not found"));

    assertThrows(RuntimeException.class, () -> deanController.createDean(invalidRequest));
    verify(deanService, times(1)).createDean(invalidRequest);
  }
}
