package edu.dosw.controller;

import edu.dosw.dto.StudentDto;
import edu.dosw.model.Student;
import edu.dosw.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/student")
@Tag(name = "Student Controller", description = "APIs for managing student")
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by ID", description = "Retrieves a student by its unique identifier")
    public ResponseEntity<Student> getStudentById(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    @PostMapping("/create")
    @Operation(summary = "Create student", description = "Creates a new student")
    public ResponseEntity<Student> createStudent(@RequestBody StudentDto studentCreationRequest) {
        return ResponseEntity.ok(studentService.createStudent(studentCreationRequest));
    }

    @PatchMapping("/update/{studentId}")
    @Operation(summary = "Update student", description = "Updates an existing student")
    public ResponseEntity<Student> updateStudent(@RequestBody StudentDto studentUpdateRequest,
                                                 @PathVariable String studentId) {
        return ResponseEntity.ok(studentService.updateStudent(studentId, studentUpdateRequest));
    }

    @DeleteMapping("/delete/{studentId}")
    @Operation(summary = "Delete student", description = "Deletes an existing student")
    public ResponseEntity<Student> deleteStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.deleteStudent(studentId));
    }
}
