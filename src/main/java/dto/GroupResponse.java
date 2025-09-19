package dto;

public record GroupResponse(
    String groupCode,
    String professor,
    int capacity,
    int enrolled
) {
    public static GroupResponse fromModel(model.Group group) {
        return new GroupResponse(
            group.getGroupCode(),
            group.getProfessor(),
            group.getCapacity(),
            group.getEnrolled()
        );
    }
}
