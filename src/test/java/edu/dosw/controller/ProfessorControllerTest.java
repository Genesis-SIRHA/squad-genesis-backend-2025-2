package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.ProfessorDto;
import edu.dosw.model.Professor;
import edu.dosw.services.UserServices.ProfessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProfessorControllerTest {

  @Mock private ProfessorService professorService;

  @InjectMocks private ProfessorController professorController;

  @Test
  void getProfessorById_WhenProfessorExists_ShouldReturnProfessor() {
    String professorId = "PROF001";
    Professor expectedProfessor =
        new Professor.ProfessorBuilder()
            .userId(professorId)
            .fullName("Dr. John Smith")
            .identityDocument("12345678")
            .email("john.smith@university.edu")
            .facultyName("Engineering")
            .build();

    when(professorService.getProfessorById(professorId)).thenReturn(expectedProfessor);

    ResponseEntity<Professor> response = professorController.getProfessorById(professorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(professorId, response.getBody().getUserId());
    assertEquals("Dr. John Smith", response.getBody().getFullName());
    verify(professorService, times(1)).getProfessorById(professorId);
  }

  @Test
  void getProfessorById_WhenProfessorNotExists_ShouldThrowException() {
    String professorId = "NONEXISTENT";
    when(professorService.getProfessorById(professorId))
        .thenThrow(new RuntimeException("Professor not found"));

    assertThrows(RuntimeException.class, () -> professorController.getProfessorById(professorId));
    verify(professorService, times(1)).getProfessorById(professorId);
  }

  @Test
  void createProfessor_WithValidData_ShouldReturnCreatedProfessor() {
    ProfessorDto professorDto = new ProfessorDto("Dr. Jane Doe", "87654321", "Science");
    Professor createdProfessor =
        new Professor.ProfessorBuilder()
            .userId("PROF002")
            .fullName("Dr. Jane Doe")
            .identityDocument("87654321")
            .email("jane.doe@university.edu")
            .facultyName("Science")
            .build();

    when(professorService.createProfessor(professorDto)).thenReturn(createdProfessor);

    ResponseEntity<Professor> response = professorController.createProfessor(professorDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("PROF002", response.getBody().getUserId());
    assertEquals("Dr. Jane Doe", response.getBody().getFullName());
    assertEquals("Science", response.getBody().getFacultyName());
    verify(professorService, times(1)).createProfessor(professorDto);
  }

  @Test
  void createProfessor_WithInvalidData_ShouldPropagateException() {
    ProfessorDto invalidProfessorDto = new ProfessorDto("", "", "");
    when(professorService.createProfessor(invalidProfessorDto))
        .thenThrow(new RuntimeException("Invalid professor data"));

    assertThrows(
        RuntimeException.class, () -> professorController.createProfessor(invalidProfessorDto));
    verify(professorService, times(1)).createProfessor(invalidProfessorDto);
  }

  @Test
  void updateProfessor_WithValidData_ShouldReturnUpdatedProfessor() {
    String professorId = "PROF001";
    ProfessorDto updateDto = new ProfessorDto("Dr. John Smith Updated", "12345678", "Engineering");
    Professor updatedProfessor =
        new Professor.ProfessorBuilder()
            .userId(professorId)
            .fullName("Dr. John Smith Updated")
            .identityDocument("12345678")
            .email("john.smith@university.edu")
            .facultyName("Engineering")
            .build();

    when(professorService.updateProfessor(professorId, updateDto)).thenReturn(updatedProfessor);

    ResponseEntity<Professor> response =
        professorController.updateProfessor(updateDto, professorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(professorId, response.getBody().getUserId());
    assertEquals("Dr. John Smith Updated", response.getBody().getFullName());
    verify(professorService, times(1)).updateProfessor(professorId, updateDto);
  }

  @Test
  void updateProfessor_WhenProfessorNotExists_ShouldThrowException() {
    String professorId = "NONEXISTENT";
    ProfessorDto updateDto = new ProfessorDto("Dr. Test", "11111111", "Test Faculty");
    when(professorService.updateProfessor(professorId, updateDto))
        .thenThrow(new RuntimeException("Professor not found"));

    assertThrows(
        RuntimeException.class, () -> professorController.updateProfessor(updateDto, professorId));
    verify(professorService, times(1)).updateProfessor(professorId, updateDto);
  }

  @Test
  void deleteProfessor_WhenProfessorExists_ShouldReturnDeletedProfessor() {
    String professorId = "PROF001";
    Professor deletedProfessor =
        new Professor.ProfessorBuilder()
            .userId(professorId)
            .fullName("Dr. John Smith")
            .identityDocument("12345678")
            .email("john.smith@university.edu")
            .facultyName("Engineering")
            .build();

    when(professorService.deleteProfessor(professorId)).thenReturn(deletedProfessor);

    ResponseEntity<Professor> response = professorController.deleteProfessor(professorId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(professorId, response.getBody().getUserId());
    assertEquals("Dr. John Smith", response.getBody().getFullName());
    verify(professorService, times(1)).deleteProfessor(professorId);
  }

  @Test
  void deleteProfessor_WhenProfessorNotExists_ShouldThrowException() {
    String professorId = "NONEXISTENT";
    when(professorService.deleteProfessor(professorId))
        .thenThrow(new RuntimeException("Professor not found"));

    assertThrows(RuntimeException.class, () -> professorController.deleteProfessor(professorId));
    verify(professorService, times(1)).deleteProfessor(professorId);
  }
}
