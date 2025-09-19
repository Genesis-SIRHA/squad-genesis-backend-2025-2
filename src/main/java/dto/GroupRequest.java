package dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GroupRequest(
    @NotBlank(message = "El código del grupo es obligatorio")
    String groupCode,
    
    @NotBlank(message = "El nombre del profesor es obligatorio")
    String professor,
    
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    int capacity,
    
    @Min(value = 0, message = "El número de inscritos no puede ser negativo")
    int enrolled
) {}
