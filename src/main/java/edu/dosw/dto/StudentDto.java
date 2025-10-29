package edu.dosw.dto;

import edu.dosw.model.enums.AcademicGrade;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StudentDto(
    @NotBlank String identityDocument,
    String fullName,
    String plan,
    String facultyName,
    AcademicGrade academicGrade,
    @Min(1) Integer semester) {}
