package edu.dosw.services;

import edu.dosw.dto.ReportDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatsService {

    private final RequestService requestService;
    private final FacultyService facultyService;
    private final GroupService groupService;

    public RequestStats getRequestStats() {
        long total = requestService.countTotalRequests();
        long pending = requestService.countByStatus(RequestStatus.PENDING);
        long approved = requestService.countByStatus(RequestStatus.ACCEPTED);
        long rejected = requestService.countByStatus(RequestStatus.REJECTED);
        return new RequestStats(total, pending, approved, rejected);
    }

    public ReportDTO getCourseReassignmentStats(String courseAbbreviation) {
        List<Group> groups = groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation);
        List<String> groupCodes = groups.stream()
                .map(Group::getGroupCode)
                .collect(Collectors.toList());

        if (groupCodes.isEmpty()) {
            return new ReportDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

        long total = requestService.countByGroupCodes(groupCodes);
        long pending = requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.PENDING);
        long approved = requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.ACCEPTED);
        long rejected = requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.REJECTED);

        long cancellations = requestService.countByGroupCodesAndType(groupCodes, RequestType.CANCELLATION);
        long swaps = requestService.countByGroupCodesAndType(groupCodes, RequestType.SWAP);
        long joins = requestService.countByGroupCodesAndType(groupCodes, RequestType.JOIN);

        return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
    }

    public ReportDTO getGroupReassignmentStats(String groupCode) {
        List<String> singleGroup = List.of(groupCode);

        long total = requestService.countByGroupCodes(singleGroup);
        long pending = requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.PENDING);
        long approved = requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.ACCEPTED);
        long rejected = requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.REJECTED);

        long cancellations = requestService.countByGroupCodesAndType(singleGroup, RequestType.CANCELLATION);
        long swaps = requestService.countByGroupCodesAndType(singleGroup, RequestType.SWAP);
        long joins = requestService.countByGroupCodesAndType(singleGroup, RequestType.JOIN);

        return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
    }

    public ReportDTO getFacultyReassignmentStats(String facultyName) {
        List<Course> facultyCourses = facultyService.findCoursesByFacultyNameAndPlan(facultyName, "2024");
        List<String> courseAbbreviations = facultyCourses.stream()
                .map(Course::getAbbreviation)
                .collect(Collectors.toList());

        if (courseAbbreviations.isEmpty()) {
            return new ReportDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

        List<String> facultyGroupCodes = courseAbbreviations.stream()
                .flatMap(abbreviation -> groupService.getAllGroupsByCourseAbbreviation(abbreviation).stream())
                .map(Group::getGroupCode)
                .distinct()
                .collect(Collectors.toList());

        if (facultyGroupCodes.isEmpty()) {
            return new ReportDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

        long total = requestService.countByGroupCodes(facultyGroupCodes);
        long pending = requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.PENDING);
        long approved = requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.ACCEPTED);
        long rejected = requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.REJECTED);

        long cancellations = requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.CANCELLATION);
        long swaps = requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.SWAP);
        long joins = requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.JOIN);

        return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
    }

    public ReportDTO getGlobalReassignmentStats() {
        long total = requestService.countTotalRequests();
        long pending = requestService.countByStatus(RequestStatus.PENDING);
        long approved = requestService.countByStatus(RequestStatus.ACCEPTED);
        long rejected = requestService.countByStatus(RequestStatus.REJECTED);

        long cancellations = requestService.countByType(RequestType.CANCELLATION);
        long swaps = requestService.countByType(RequestType.SWAP);
        long joins = requestService.countByType(RequestType.JOIN);

        return new ReportDTO(total, pending, approved, rejected, cancellations, swaps, joins);
    }
}