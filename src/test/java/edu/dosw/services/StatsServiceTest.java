package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.ReportDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

  @Mock private RequestService requestService;

  @Mock private FacultyService facultyService;

  @Mock private GroupService groupService;

  @InjectMocks private StatsService statsService;

  @Test
  void getRequestStats_ShouldReturnCorrectStats() {
    when(requestService.countTotalRequests()).thenReturn(100);
    when(requestService.countByStatus(RequestStatus.PENDING)).thenReturn(20);
    when(requestService.countByStatus(RequestStatus.ACCEPTED)).thenReturn(70);
    when(requestService.countByStatus(RequestStatus.REJECTED)).thenReturn(10);

    RequestStats result = statsService.getRequestStats();

    assertNotNull(result);
    assertEquals(100, result.total());
    assertEquals(20, result.pending());
    assertEquals(70, result.approved());
    assertEquals(10, result.rejected());
  }

  @Test
  void getCourseReassignmentStats_ShouldReturnCorrectStats() {
    String courseAbbreviation = "MATH101";
    List<Group> groups =
            List.of(createGroup("MATH101-G01", "MATH101"), createGroup("MATH101-G02", "MATH101"));
    List<String> groupCodes = List.of("MATH101-G01", "MATH101-G02");

    when(groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation)).thenReturn(groups);
    when(requestService.countByGroupCodes(groupCodes)).thenReturn(50);
    when(requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.PENDING))
            .thenReturn(10);
    when(requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.ACCEPTED))
            .thenReturn(30);
    when(requestService.countByGroupCodesAndStatus(groupCodes, RequestStatus.REJECTED))
            .thenReturn(10);
    when(requestService.countByGroupCodesAndType(groupCodes, RequestType.CANCELLATION))
            .thenReturn(20);
    when(requestService.countByGroupCodesAndType(groupCodes, RequestType.SWAP)).thenReturn(25);
    when(requestService.countByGroupCodesAndType(groupCodes, RequestType.JOIN)).thenReturn(25);

    ReportDTO result = statsService.getCourseReassignmentStats(courseAbbreviation);

    assertNotNull(result);
    assertEquals(50, result.total());
    assertEquals(10, result.pending());
    assertEquals(30, result.approved());
    assertEquals(10, result.rejected());
    assertEquals(20, result.cancellations());
    assertEquals(25, result.swaps());
    assertEquals(25, result.joins());
  }

  @Test
  void getCourseReassignmentStats_WhenNoGroups_ShouldReturnZeroStats() {
    String courseAbbreviation = "UNKNOWN";
    when(groupService.getAllGroupsByCourseAbbreviation(courseAbbreviation)).thenReturn(List.of());

    ReportDTO result = statsService.getCourseReassignmentStats(courseAbbreviation);

    assertNotNull(result);
    assertEquals(0, result.total());
    assertEquals(0, result.pending());
    assertEquals(0, result.approved());
    assertEquals(0, result.rejected());
    assertEquals(0, result.cancellations());
    assertEquals(0, result.swaps());
    assertEquals(0, result.joins());
  }

  @Test
  void getGroupReassignmentStats_ShouldReturnCorrectStats() {
    String groupCode = "MATH101-G01";
    List<String> singleGroup = List.of(groupCode);

    when(requestService.countByGroupCodes(singleGroup)).thenReturn(40);
    when(requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.PENDING))
            .thenReturn(5);
    when(requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.ACCEPTED))
            .thenReturn(30);
    when(requestService.countByGroupCodesAndStatus(singleGroup, RequestStatus.REJECTED))
            .thenReturn(5);
    when(requestService.countByGroupCodesAndType(singleGroup, RequestType.CANCELLATION))
            .thenReturn(10);
    when(requestService.countByGroupCodesAndType(singleGroup, RequestType.SWAP)).thenReturn(15);
    when(requestService.countByGroupCodesAndType(singleGroup, RequestType.JOIN)).thenReturn(5);

    ReportDTO result = statsService.getGroupReassignmentStats(groupCode);

    assertNotNull(result);
    assertEquals(40, result.total());
    assertEquals(5, result.pending());
    assertEquals(30, result.approved());
    assertEquals(5, result.rejected());
    assertEquals(10, result.cancellations());
    assertEquals(15, result.swaps());
    assertEquals(5, result.joins());
  }

  @Test
  void getFacultyReassignmentStats_ShouldReturnCorrectStats() {
    String facultyName = "Engineering";
    String plan = "2024";
    List<Course> facultyCourses =
            List.of(new Course("MATH101", "Mathematics", 3), new Course("PHYS101", "Physics", 4));
    List<Group> mathGroups = List.of(createGroup("MATH101-G01", "MATH101"));
    List<Group> physicsGroups = List.of(createGroup("PHYS101-G01", "PHYS101"));
    List<String> facultyGroupCodes = List.of("MATH101-G01", "PHYS101-G01");

    when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan))
            .thenReturn(facultyCourses);
    when(groupService.getAllGroupsByCourseAbbreviation("MATH101")).thenReturn(mathGroups);
    when(groupService.getAllGroupsByCourseAbbreviation("PHYS101")).thenReturn(physicsGroups);
    when(requestService.countByGroupCodes(facultyGroupCodes)).thenReturn(15);
    when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.PENDING))
            .thenReturn(3);
    when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.ACCEPTED))
            .thenReturn(10);
    when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.REJECTED))
            .thenReturn(2);
    when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.CANCELLATION))
            .thenReturn(4);
    when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.SWAP))
            .thenReturn(5);
    when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.JOIN))
            .thenReturn(1);

    ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

    assertNotNull(result);
    assertEquals(15, result.total());
    assertEquals(3, result.pending());
    assertEquals(10, result.approved());
    assertEquals(2, result.rejected());
    assertEquals(4, result.cancellations());
    assertEquals(5, result.swaps());
    assertEquals(1, result.joins());
  }

  @Test
  void getFacultyReassignmentStats_WhenNoCourses_ShouldReturnZeroStats() {
    String facultyName = "Unknown";
    String plan = "2024";
    when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan)).thenReturn(List.of());

    ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

    assertNotNull(result);
    assertEquals(0, result.total());
    assertEquals(0, result.pending());
    assertEquals(0, result.approved());
    assertEquals(0, result.rejected());
    assertEquals(0, result.cancellations());
    assertEquals(0, result.swaps());
    assertEquals(0, result.joins());
  }

  @Test
  void getFacultyReassignmentStats_WhenNoGroups_ShouldReturnZeroStats() {
    String facultyName = "Engineering";
    String plan = "2023";
    List<Course> facultyCourses =
            List.of(new Course("MATH101", "Mathematics", 3), new Course("PHYS101", "Physics", 4));

    when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan))
            .thenReturn(facultyCourses);
    when(groupService.getAllGroupsByCourseAbbreviation("MATH101")).thenReturn(List.of());
    when(groupService.getAllGroupsByCourseAbbreviation("PHYS101")).thenReturn(List.of());

    ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

    assertNotNull(result);
    assertEquals(0, result.total());
    assertEquals(0, result.pending());
    assertEquals(0, result.approved());
    assertEquals(0, result.rejected());
    assertEquals(0, result.cancellations());
    assertEquals(0, result.swaps());
    assertEquals(0, result.joins());
  }

  @Test
  void getFacultyReassignmentStats_WithDifferentPlan_ShouldUseCorrectPlan() {
    String facultyName = "Science";
    String plan = "2025B";
    List<Course> facultyCourses = List.of(new Course("CHEM101", "Chemistry", 3));
    List<Group> chemGroups = List.of(createGroup("CHEM101-G01", "CHEM101"));
    List<String> facultyGroupCodes = List.of("CHEM101-G01");

    when(facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan))
            .thenReturn(facultyCourses);
    when(groupService.getAllGroupsByCourseAbbreviation("CHEM101")).thenReturn(chemGroups);
    when(requestService.countByGroupCodes(facultyGroupCodes)).thenReturn(8);
    when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.PENDING))
            .thenReturn(1);
    when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.ACCEPTED))
            .thenReturn(6);
    when(requestService.countByGroupCodesAndStatus(facultyGroupCodes, RequestStatus.REJECTED))
            .thenReturn(1);
    when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.CANCELLATION))
            .thenReturn(2);
    when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.SWAP))
            .thenReturn(3);
    when(requestService.countByGroupCodesAndType(facultyGroupCodes, RequestType.JOIN))
            .thenReturn(3);

    ReportDTO result = statsService.getFacultyReassignmentStats(facultyName, plan);

    assertNotNull(result);
    assertEquals(8, result.total());
    assertEquals(1, result.pending());
    assertEquals(6, result.approved());
    assertEquals(1, result.rejected());
    assertEquals(2, result.cancellations());
    assertEquals(3, result.swaps());
    assertEquals(3, result.joins());

    verify(facultyService).findCoursesByFacultyNameAndPlan(facultyName, plan);
  }

  @Test
  void getGlobalReassignmentStats_ShouldReturnCorrectStats() {
    when(requestService.countTotalRequests()).thenReturn(100);
    when(requestService.countByStatus(RequestStatus.PENDING)).thenReturn(20);
    when(requestService.countByStatus(RequestStatus.ACCEPTED)).thenReturn(70);
    when(requestService.countByStatus(RequestStatus.REJECTED)).thenReturn(10);
    when(requestService.countByType(RequestType.CANCELLATION)).thenReturn(30);
    when(requestService.countByType(RequestType.SWAP)).thenReturn(40);
    when(requestService.countByType(RequestType.JOIN)).thenReturn(30);

    ReportDTO result = statsService.getGlobalReassignmentStats();

    assertNotNull(result);
    assertEquals(100, result.total());
    assertEquals(20, result.pending());
    assertEquals(70, result.approved());
    assertEquals(10, result.rejected());
    assertEquals(30, result.cancellations());
    assertEquals(40, result.swaps());
    assertEquals(30, result.joins());
  }

  private Group createGroup(String groupCode, String abbreviation) {
    Group group = new Group();
    group.setGroupCode(groupCode);
    group.setAbbreviation(abbreviation);
    return group;
  }
}