package edu.dosw.controller;

import edu.dosw.dto.StudentDto;
import edu.dosw.model.Student;
import edu.dosw.services.UserServices.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/student")
@Tag(name = "Student Controller", description = "APIs for managing student")
public class StudentController {
  private final StudentService studentService;

  /**
   * Retrieves a student by their unique identifier
   *
   * @param studentId The unique identifier of the student
   * @return ResponseEntity containing the student data
   */
  @GetMapping("/{studentId}")
  @PreAuthorize(
      "hasAnyRole('ADMINISTRATOR', 'DEAN', 'PROFESSOR', 'STUDENT') and @authenticationService.canAccessStudentData(authentication, #studentId)")
  @Operation(
      summary = "Get student by ID",
      description = "Retrieves a student by its unique identifier")
  public ResponseEntity<Student> getStudentById(@PathVariable String studentId) {
    return ResponseEntity.ok(studentService.getStudentById(studentId));
  }

  /**
   * Creates a new student with the provided data
   *
   * @param studentCreationRequest The DTO containing student creation data
   * @return ResponseEntity containing the created student
   */
  @PostMapping("/create")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Create student", description = "Creates a new student")
  public ResponseEntity<Student> createStudent(@RequestBody StudentDto studentCreationRequest) {
    return ResponseEntity.ok(studentService.createStudent(studentCreationRequest));
  }

  /**
   * Updates an existing student with the provided data
   *
   * @param studentUpdateRequest The DTO containing student update data
   * @param studentId The unique identifier of the student to update
   * @return ResponseEntity containing the updated student
   */
  @PatchMapping("/update/{studentId}")
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DEAN')")
  @Operation(summary = "Update student", description = "Updates an existing student")
  public ResponseEntity<Student> updateStudent(
      @RequestBody StudentDto studentUpdateRequest, @PathVariable String studentId) {
    return ResponseEntity.ok(studentService.updateStudent(studentId, studentUpdateRequest));
  }

  /**
   * Deletes a student by their unique identifier
   *
   * @param studentId The unique identifier of the student to delete
   * @return ResponseEntity containing the deleted student
   */
  @DeleteMapping("/delete/{studentId}")
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  @Operation(summary = "Delete student", description = "Deletes an existing student")
  public ResponseEntity<Student> deleteStudent(@PathVariable String studentId) {
    return ResponseEntity.ok(studentService.deleteStudent(studentId));
  }
}
