package edu.dosw.dto;

import edu.dosw.model.Group;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a request to create or update a group.
 * Contains group details like code, professor, capacity, and enrolled students.
 *
 * @param groupCode Unique code that identifies the group. Cannot be blank.
 * @param abbreviation Abbreviation of the course. Cannot be blank.
 * @param year Academic year. Cannot be blank.
 * @param period Academic period. Cannot be blank.
 * @param teacherId ID of the professor for this group. Cannot be blank.
 * @param isLab Indicates if the group is a lab. Cannot be null.
 * @param groupNum Group number. Must be greater than 0.
 * @param maxCapacity Maximum capacity of the group. Must be greater than 0.
 * @param enrolled Number of currently enrolled students. Cannot be negative.
 */
public record GroupRequest(
    @NotBlank(message = "El código del grupo es obligatorio")
    String groupCode,
    
    @NotBlank(message = "La abreviatura del curso es obligatoria")
    String abbreviation,
    
    @NotBlank(message = "El año académico es obligatorio")
    String year,
    
    @NotBlank(message = "El período académico es obligatorio")
    String period,
    
    @NotBlank(message = "El ID del profesor es obligatorio")
    String teacherId,
    
    @NotNull(message = "El indicador de laboratorio es obligatorio")
    Boolean isLab,
    
    @Min(value = 1, message = "El número de grupo debe ser mayor a 0")
    int groupNum,
    
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    int maxCapacity,
    
    @Min(value = 0, message = "El número de inscritos no puede ser negativo")
    int enrolled
) {
    /**
     * Converts this GroupRequest to a Group entity.
     *
     * @return a new Group entity populated with this request's data
     */
    public Group toEntity() {
        Group group = new Group();
        group.setGroupCode(this.groupCode);
        group.setAbbreviation(this.abbreviation);
        group.setYear(this.year);
        group.setPeriod(this.period);
        group.setTeacherId(this.teacherId);
        group.setLab(this.isLab);
        group.setGroupNum(this.groupNum);
        group.setMaxCapacity(this.maxCapacity);
        group.setEnrolled(this.enrolled);
        return group;
    }
}
