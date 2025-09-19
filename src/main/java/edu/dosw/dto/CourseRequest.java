package edu.dosw.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CourseRequest(
    @NotBlank(message = "El código del curso es obligatorio")
    String code,
    
    @NotBlank(message = "El nombre del curso es obligatorio")
    String name,
    
    @Valid
    List<GroupRequest> groups
) {}
