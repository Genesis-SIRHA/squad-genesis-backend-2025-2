package edu.dosw.dto;

import edu.dosw.model.Group;

public record GroupResponse(
    String groupCode,
    String professor,
    int capacity,
    int enrolled
) {
    public static GroupResponse fromModel(Group group) {
        return new GroupResponse(
            group.getGroupCode(),
            group.getTeacherId(),
            group.getCapacity(),
            group.getEnrolled()
        );
    }
}
