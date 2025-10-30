package edu.dosw.services;

import edu.dosw.dto.ReportDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatsService {

  private final RequestService requestService;
  private final FacultyService facultyService;
  private final GroupService groupService;

  public RequestStats getRequestStats() {
    Integer total = requestService.countTotalRequests();
    Integer pending = requestService.countByStatus(RequestStatus.PENDING);
    Integer approved = requestService.countByStatus(RequestStatus.ACCEPTED);
    Integer rejected = requestService.countByStatus(RequestStatus.REJECTED);
    return new RequestStats(total, pending, approved, rejected);
  }

  public ReportDTO getCourseReassignmentStats(String courseAbbreviation) {
    List<Group> groups = groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation);

    if (groups.isEmpty()) {
      return new ReportDTO(0, 0, 0, 0, 0, 0, 0);
    }

    List<String> groupCodes = groups.stream().map(Group::getGroupCode).collect(Collectors.toList());

    Integer total = requestService.countByGroupCodes(groupCodes);
    Integer pending = requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.PENDING);
    Integer approved =
        requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.ACCEPTED);
    Integer rejected =
        requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.REJECTED);

    Integer cancellations =
        requestService.countByGroupCodesAndType(groupCodes, RequestType.CANCELLATION);
    Integer swaps = requestService.countByGroupCodesAndType(groupCodes, RequestType.SWAP);
    Integer joins = requestService.countByGroupCodesAndType(groupCodes, RequestType.JOIN);

    return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
  }

  public ReportDTO getGroupReassignmentStats(String groupCode) {
    List<String> singleGroup = List.of(groupCode);

    Integer total = requestService.countByGroupCodes(singleGroup);
    Integer pending = requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.PENDING);
    Integer approved =
        requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.ACCEPTED);
    Integer rejected =
        requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.REJECTED);

    Integer cancellations =
        requestService.countByGroupCodesAndType(singleGroup, RequestType.CANCELLATION);
    Integer swaps = requestService.countByGroupCodesAndType(singleGroup, RequestType.SWAP);
    Integer joins = requestService.countByGroupCodesAndType(singleGroup, RequestType.JOIN);

    return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
  }

  public ReportDTO getFacultyReassignmentStats(String facultyName, String plan) {
    List<Course> facultyCourses = facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan);
    List<String> courseAbbreviations =
        facultyCourses.stream().map(Course::getAbbreviation).collect(Collectors.toList());

    if (courseAbbreviations.isEmpty()) {
      return new ReportDTO(0, 0, 0, 0, 0, 0, 0);
    }

    List<String> facultyGroupCodes =
        courseAbbreviations.stream()
            .flatMap(
                abbreviation ->
                    groupService.getAllGroupsByCourseAbbreviation(abbreviation).stream())
            .map(Group::getGroupCode)
            .distinct()
            .collect(Collectors.toList());

    if (facultyGroupCodes.isEmpty()) {
      return new ReportDTO(0, 0, 0, 0, 0, 0, 0);
    }

    Integer total = requestService.countByGroupCodes(facultyGroupCodes);
    Integer pending =
        requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.PENDING);
    Integer approved =
        requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.ACCEPTED);
    Integer rejected =
        requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.REJECTED);

    Integer cancellations =
        requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.CANCELLATION);
    Integer swaps = requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.SWAP);
    Integer joins = requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.JOIN);

    return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
  }

  public ReportDTO getGlobalReassignmentStats() {
    Integer total = requestService.countTotalRequests();
    Integer pending = requestService.countByStatus(RequestStatus.PENDING);
    Integer approved = requestService.countByStatus(RequestStatus.ACCEPTED);
    Integer rejected = requestService.countByStatus(RequestStatus.REJECTED);

    Integer cancellations = requestService.countByType(RequestType.CANCELLATION);
    Integer swaps = requestService.countByType(RequestType.SWAP);
    Integer joins = requestService.countByType(RequestType.JOIN);

    return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
  }
}
