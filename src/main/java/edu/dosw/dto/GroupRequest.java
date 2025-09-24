package edu.dosw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a request to create or update a group.
 * Contains group details like code, professor, capacity, and enrolled students.
 */
public record GroupRequest(
    /** Unique code that identifies the group. Cannot be blank. */
    @NotBlank(message = "El código del grupo es obligatorio")
    String groupCode,
    
    /** Name of the professor for this group. Cannot be blank. */
    @NotBlank(message = "El nombre del profesor es obligatorio")
    String professor,
    
    /** Maximum capacity of the group. Must be greater than 0. */
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    int capacity,
    
    /** Number of currently enrolled students. Cannot be negative. */
    @Min(value = 0, message = "El número de inscritos no puede ser negativo")
    int enrolled
) {}
