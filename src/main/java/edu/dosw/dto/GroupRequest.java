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
    @NotBlank(message = "Group code is required")
    String groupCode,
    
    @NotBlank(message = "Course abbreviation is required")
    String abbreviation,
    
    @NotBlank(message = "Academic year is required")
    String year,
    
    @NotBlank(message = "Academic period is required")
    String period,
    
    @NotBlank(message = "Professor ID is required")
    String teacherId,
    
    @NotNull(message = "Lab indicator is required")
    Boolean isLab,
    
    @Min(value = 1, message = "Group number must be greater than 0")
    int groupNum,
    
    @Min(value = 1, message = "Capacity must be greater than 0")
    int maxCapacity,
    
    @Min(value = 0, message = "Number of enrolled students cannot be negative")
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
