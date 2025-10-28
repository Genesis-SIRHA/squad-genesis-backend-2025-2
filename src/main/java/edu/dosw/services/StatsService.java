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
    int total = (int) requestService.countTotalRequests();
    int pending = (int) requestService.countByStatus(RequestStatus.PENDING);
    int approved = (int) requestService.countByStatus(RequestStatus.ACCEPTED);
    int rejected = (int) requestService.countByStatus(RequestStatus.REJECTED);
    return new RequestStats(total, pending, approved, rejected);
  }

  public ReportDTO getCourseReassignmentStats(String courseAbbreviation) {
    List<Group> groups = groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation);
    List<String> groupCodes = groups.stream().map(Group::getGroupCode).collect(Collectors.toList());

    if (groupCodes.isEmpty()) {
      return new ReportDTO(0, 0, 0, 0, 0, 0, 0);
    }

    int total = (int) requestService.countByGroupCodes(groupCodes);
    int pending =
        (int) requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.PENDING);
    int approved =
        (int) requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.ACCEPTED);
    int rejected =
        (int) requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.REJECTED);

    int cancellations =
        (int) requestService.countByGroupCodesAndType(groupCodes, RequestType.CANCELLATION);
    int swaps = (int) requestService.countByGroupCodesAndType(groupCodes, RequestType.SWAP);
    int joins = (int) requestService.countByGroupCodesAndType(groupCodes, RequestType.JOIN);

    return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
  }

  public ReportDTO getGroupReassignmentStats(String groupCode) {
    List<String> singleGroup = List.of(groupCode);

    int total = (int) requestService.countByGroupCodes(singleGroup);
    int pending =
        (int) requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.PENDING);
    int approved =
        (int) requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.ACCEPTED);
    int rejected =
        (int) requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.REJECTED);

    int cancellations =
        (int) requestService.countByGroupCodesAndType(singleGroup, RequestType.CANCELLATION);
    int swaps = (int) requestService.countByGroupCodesAndType(singleGroup, RequestType.SWAP);
    int joins = (int) requestService.countByGroupCodesAndType(singleGroup, RequestType.JOIN);

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

    int total = (int) requestService.countByGroupCodes(facultyGroupCodes);
    int pending =
        (int) requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.PENDING);
    int approved =
        (int) requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.ACCEPTED);
    int rejected =
        (int) requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.REJECTED);

    int cancellations =
        (int) requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.CANCELLATION);
    int swaps = (int) requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.SWAP);
    int joins = (int) requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.JOIN);

    return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
  }

  public ReportDTO getGlobalReassignmentStats() {
    int total = (int) requestService.countTotalRequests();
    int pending = (int) requestService.countByStatus(RequestStatus.PENDING);
    int approved = (int) requestService.countByStatus(RequestStatus.ACCEPTED);
    int rejected = (int) requestService.countByStatus(RequestStatus.REJECTED);

    int cancellations = (int) requestService.countByType(RequestType.CANCELLATION);
    int swaps = (int) requestService.countByType(RequestType.SWAP);
    int joins = (int) requestService.countByType(RequestType.JOIN);

    return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
  }
}
