package edu.dosw.services;

import edu.dosw.dto.ReportDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private FacultyService facultyService;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private StatsService statsService;

    @Test
    void getRequestStats_ShouldReturnCorrectStats() {
        when(requestService.countTotalRequests()).thenReturn(100L);
        when(requestService.countByStatus(RequestStatus.PENDING)).thenReturn(20L);
        when(requestService.countByStatus(RequestStatus.ACCEPTED)).thenReturn(70L);
        when(requestService.countByStatus(RequestStatus.REJECTED)).thenReturn(10L);

        RequestStats result = statsService.getRequestStats();

        assertNotNull(result);
        assertEquals(100L, result.total());
        assertEquals(20L, result.pending());
        assertEquals(70L, result.approved());
        assertEquals(10L, result.rejected());
    }

    @Test
    void getCourseReassignmentStats_ShouldReturnCorrectStats() {
        String courseAbbreviation = "MATH101";
        List<Group> groups = List.of(
                createGroup("MATH101-G01", "MATH101"),
                createGroup("MATH101-G02", "MATH101")
        );
        List<String> groupCodes = List.of("MATH101-G01", "MATH101-G02");

        when(groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation)).thenReturn(groups);
        when(requestService.countByGroupCodes(groupCodes)).thenReturn(50L);
        when(requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.PENDING)).thenReturn(10L);
        when(requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.ACCEPTED)).thenReturn(30L);
        when(requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.REJECTED)).thenReturn(10L);
        when(requestService.countByGroupCodesAndType(groupCodes, RequestType.CANCELLATION)).thenReturn(20L);
        when(requestService.countByGroupCodesAndType(groupCodes, RequestType.SWAP)).thenReturn(25L);
        when(requestService.countByGroupCodesAndType(groupCodes, RequestType.JOIN)).thenReturn(25L);

        ReportDTO result = statsService.getCourseReassignmentStats(courseAbbreviation);

        assertNotNull(result);
        assertEquals(50L, result.total());
        assertEquals(10L, result.pending());
        assertEquals(30L, result.approved());
        assertEquals(10L, result.rejected());
        assertEquals(20L, result.cancellations());
        assertEquals(25L, result.swaps());
        assertEquals(25L, result.joins());
    }

    @Test
    void getCourseReassignmentStats_WhenNoGroups_ShouldReturnZeroStats() {
        String courseAbbreviation = "UNKNOWN";
        when(groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation)).thenReturn(List.of());

        ReportDTO result = statsService.getCourseReassignmentStats(courseAbbreviation);

        assertNotNull(result);
        assertEquals(0L, result.total());
        assertEquals(0L, result.pending());
        assertEquals(0L, result.approved());
        assertEquals(0L, result.rejected());
        assertEquals(0L, result.cancellations());
        assertEquals(0L, result.swaps());
        assertEquals(0L, result.joins());
    }

    @Test
    void getGroupReassignmentStats_ShouldReturnCorrectStats() {
        String groupCode = "MATH101-G01";
        List<String> singleGroup = List.of(groupCode);

        when(requestService.countByGroupCodes(singleGroup)).thenReturn(40L);
        when(requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.PENDING)).thenReturn(5L);
        when(requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.ACCEPTED)).thenReturn(30L);
        when(requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.REJECTED)).thenReturn(5L);
        when(requestService.countByGroupCodesAndType(singleGroup, RequestType.CANCELLATION)).thenReturn(10L);
        when(requestService.countByGroupCodesAndType(singleGroup, RequestType.SWAP)).thenReturn(15L);
        when(requestService.countByGroupCodesAndType(singleGroup, RequestType.JOIN)).thenReturn(5L);

        ReportDTO result = statsService.getGroupReassignmentStats(groupCode);

        assertNotNull(result);
        assertEquals(40L, result.total());
        assertEquals(5L, result.pending());
        assertEquals(30L, result.approved());
        assertEquals(5L, result.rejected());
        assertEquals(10L, result.cancellations());
        assertEquals(15L, result.swaps());
        assertEquals(5L, result.joins());
    }

    @Test
    void getFacultyReassignmentStats_ShouldReturnCorrectStats() {
        String facultyName = "Engineering";
        String plan = "2024";
        List<Course> facultyCourses = List.of(
                new Course("MATH101", "Mathematics", 3),
                new Course("PHYS101", "Physics", 4)
        );
        List<Group> mathGroups = List.of(createGroup("MATH101-G01", "MATH101"));
        List<Group> physicsGroups = List.of(createGroup("PHYS101-G01", "PHYS101"));
        List<String> facultyGroupCodes = List.of("MATH101-G01", "PHYS101-G01");

        when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan)).thenReturn(facultyCourses);
        when(groupService.getAllGroupsByCourseAbbreviation("MATH101")).thenReturn(mathGroups);
        when(groupService.getAllGroupsByCourseAbbreviation("PHYS101")).thenReturn(physicsGroups);
        when(requestService.countByGroupCodes(facultyGroupCodes)).thenReturn(15L);
        when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.PENDING)).thenReturn(3L);
        when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.ACCEPTED)).thenReturn(10L);
        when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.REJECTED)).thenReturn(2L);
        when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.CANCELLATION)).thenReturn(4L);
        when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.SWAP)).thenReturn(5L);
        when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.JOIN)).thenReturn(1L);

        ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

        assertNotNull(result);
        assertEquals(15L, result.total());
        assertEquals(3L, result.pending());
        assertEquals(10L, result.approved());
        assertEquals(2L, result.rejected());
        assertEquals(4L, result.cancellations());
        assertEquals(5L, result.swaps());
        assertEquals(1L, result.joins());
    }

    @Test
    void getFacultyReassignmentStats_WhenNoCourses_ShouldReturnZeroStats() {
        String facultyName = "Unknown";
        String plan = "2024";
        when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan)).thenReturn(List.of());

        ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

        assertNotNull(result);
        assertEquals(0L, result.total());
        assertEquals(0L, result.pending());
        assertEquals(0L, result.approved());
        assertEquals(0L, result.rejected());
        assertEquals(0L, result.cancellations());
        assertEquals(0L, result.swaps());
        assertEquals(0L, result.joins());
    }

    @Test
    void getFacultyReassignmentStats_WhenNoGroups_ShouldReturnZeroStats() {
        String facultyName = "Engineering";
        String plan = "2023";
        List<Course> facultyCourses = List.of(
                new Course("MATH101", "Mathematics", 3),
                new Course("PHYS101", "Physics", 4)
        );

        when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan)).thenReturn(facultyCourses);
        when(groupService.getAllGroupsByCourseAbbreviation("MATH101")).thenReturn(List.of());
        when(groupService.getAllGroupsByCourseAbbreviation("PHYS101")).thenReturn(List.of());

        ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

        assertNotNull(result);
        assertEquals(0L, result.total());
        assertEquals(0L, result.pending());
        assertEquals(0L, result.approved());
        assertEquals(0L, result.rejected());
        assertEquals(0L, result.cancellations());
        assertEquals(0L, result.swaps());
        assertEquals(0L, result.joins());
    }

    @Test
    void getFacultyReassignmentStats_WithDifferentPlan_ShouldUseCorrectPlan() {
        String facultyName = "Science";
        String plan = "2025B";
        List<Course> facultyCourses = List.of(
                new Course("CHEM101", "Chemistry", 3)
        );
        List<Group> chemGroups = List.of(createGroup("CHEM101-G01", "CHEM101"));
        List<String> facultyGroupCodes = List.of("CHEM101-G01");

        when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan)).thenReturn(facultyCourses);
        when(groupService.getAllGroupsByCourseAbbreviation("CHEM101")).thenReturn(chemGroups);
        when(requestService.countByGroupCodes(facultyGroupCodes)).thenReturn(8L);
        when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.PENDING)).thenReturn(1L);
        when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.ACCEPTED)).thenReturn(6L);
        when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.REJECTED)).thenReturn(1L);
        when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.CANCELLATION)).thenReturn(2L);
        when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.SWAP)).thenReturn(3L);
        when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.JOIN)).thenReturn(3L);

        ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

        assertNotNull(result);
        assertEquals(8L, result.total());
        assertEquals(1L, result.pending());
        assertEquals(6L, result.approved());
        assertEquals(1L, result.rejected());
        assertEquals(2L, result.cancellations());
        assertEquals(3L, result.swaps());
        assertEquals(3L, result.joins());

        verify(facultyService).findCoursesByFacultyNameAndPlan(facultyName, plan);
    }

    @Test
    void getGlobalReassignmentStats_ShouldReturnCorrectStats() {
        when(requestService.countTotalRequests()).thenReturn(100L);
        when(requestService.countByStatus(RequestStatus.PENDING)).thenReturn(20L);
        when(requestService.countByStatus(RequestStatus.ACCEPTED)).thenReturn(70L);
        when(requestService.countByStatus(RequestStatus.REJECTED)).thenReturn(10L);
        when(requestService.countByType(RequestType.CANCELLATION)).thenReturn(30L);
        when(requestService.countByType(RequestType.SWAP)).thenReturn(40L);
        when(requestService.countByType(RequestType.JOIN)).thenReturn(30L);

        ReportDTO result = statsService.getGlobalReassignmentStats();

        assertNotNull(result);
        assertEquals(100L, result.total());
        assertEquals(20L, result.pending());
        assertEquals(70L, result.approved());
        assertEquals(10L, result.rejected());
        assertEquals(30L, result.cancellations());
        assertEquals(40L, result.swaps());
        assertEquals(30L, result.joins());
    }

    private Group createGroup(String groupCode, String abbreviation) {
        Group group = new Group();
        group.setGroupCode(groupCode);
        group.setAbbreviation(abbreviation);
        return group;
    }
}