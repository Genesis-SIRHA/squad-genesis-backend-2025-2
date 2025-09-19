package dto;

import model.Course;
import java.util.List;

public record CourseResponse(
    String id,
    String code,
    String name,
    List<GroupResponse> groups
) {
    public static CourseResponse fromModel(Course course) {
        return new CourseResponse(
            course.getId(),
            course.getCode(),
            course.getName(),
            course.getGroups().stream()
                .map(GroupResponse::fromModel)
                .toList()
        );
    }
}
