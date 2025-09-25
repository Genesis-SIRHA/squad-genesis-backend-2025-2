package edu.dosw.dto;

import edu.dosw.model.Group;

/**
 * Represents the response containing group information.
 * Includes group details like code, professor, capacity, and enrolled students.
 */
public record GroupResponse(
    /** Unique code that identifies the group */
    String groupCode,
    
    /** Name of the professor for this group */
    String professor,
    
    /** Maximum capacity of the group */
    int capacity,
    
    /** Number of currently enrolled students */
    int enrolled
) {
    /**
     * Creates a GroupResponse from a Group entity.
     * @param group The group entity to convert
     * @return A new GroupResponse instance
     */
    public static GroupResponse fromModel(Group group) {
        return new GroupResponse(
            group.getGroupCode(),
            group.getProfessor(),
            group.getCapacity(),
            group.getEnrolled()
        );
    }

    public String getGroupCode() {
        return groupCode;
    }
}
