package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.RequestStats;
import edu.dosw.dto.UpdateRequestDto;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import edu.dosw.model.enums.Role;
import edu.dosw.model.enums.Status;
import edu.dosw.repositories.FacultyRepository;
import edu.dosw.repositories.GroupRepository;
import edu.dosw.repositories.RequestRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link RequestService}. */
class RequestServiceTest {

  private RequestRepository requestRepository;
  private FacultyRepository facultyRepository;
  private GroupRepository groupRepository;
  private RequestService requestService;
  private DeanService deanService;
  private ProfessorService professorSevice;
  private ValidatorService validatorService;
  private StudentService studentService;
  private AuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    requestRepository = mock(RequestRepository.class);
    facultyRepository = mock(FacultyRepository.class);
    groupRepository = mock(GroupRepository.class);
    deanService = mock(DeanService.class);
    professorSevice = mock(ProfessorService.class);
    studentService = mock(StudentService.class);
    validatorService = mock(ValidatorService.class);
    requestService =
        new RequestService(
            requestRepository,validatorService,deanService,professorSevice,studentService, authenticationService);
  }

  @Test
  void fetchRequests_shouldReturnSortedRequestsForStudent() {
    Request oldReq = new Request();
    oldReq.setCreatedAt(LocalDateTime.now().minusDays(1));
    Request newReq = new Request();
    newReq.setCreatedAt(LocalDateTime.now());

    when(requestRepository.findByStudentId("s1")).thenReturn(List.of(oldReq, newReq));

    List<Request> result = requestService.fetchRequests(Role.STUDENT, "s1");

    assertEquals(2, result.size());
    assertEquals(newReq, result.get(0));
  }

  @Test
  void fetchRequests_shouldThrowForInvalidRole() {
    assertThrows(
        IllegalArgumentException.class,
        () -> requestService.fetchRequests(Role.valueOf("INVALID"), "s1"));
  }

//  @Test
//  void createRequest_shouldCreateRequestWhenGroupsExist() {
//    CreateRequestDto dto =
//        new CreateRequestDto(
//            "student1",
//            "TYPE_A",
//            "desc",
//            "originGroup",
//            "destGroup"
//        );
//
//    Group origin = new Group();
//    origin.setGroupCode("originGroup");
//    Group dest = new Group();
//    dest.setGroupCode("destGroup");
//
//    when(groupRepository.findByGroupCode("originGroup")).thenReturn(origin);
//    when(groupRepository.findByGroupCode("destGroup")).thenReturn(dest);
//    when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));
//
//    Request result = requestService.createRequest(dto);
//
//    assertNotNull(result);
//    assertEquals("student1", result.getStudentId());
//    assertEquals("originGroup", result.getOriginGroupId());
//    assertEquals("destGroup", result.getDestinationGroupId());
//    assertEquals(Status.PENDING, result.getStatus());
//    assertFalse(result.getIsExceptional());
//  }

  @Test
  void createRequest_shouldThrowWhenOriginGroupNotFound() {
    CreateRequestDto dto =
        new CreateRequestDto(
            "student1",
            "TYPE_A",
            "desc",
            "invalidGroup",
            "destGroup"
        );

    when(groupRepository.findByGroupCode("invalidGroup")).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(dto));
  }

//  @Test
//  void createRequest_shouldThrowWhenDestinationGroupNotFound() {
//    CreateRequestDto dto =
//        new CreateRequestDto(
//            "student1",
//            "TYPE_A",
//            "desc",
//            "originGroup",
//            "invalidGroup"
//        );
//
//    Group origin = new Group();
//    origin.setGroupCode("originGroup");
//
//    when(groupRepository.findByGroupCode("originGroup")).thenReturn(origin);
//    when(groupRepository.findByGroupCode("invalidGroup")).thenReturn(null);
//
//    assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(dto));
//  }


  @Test
  void getRequestStats_shouldReturnStats() {
    when(requestRepository.count()).thenReturn(10L);
    when(requestRepository.countByStatus("PENDING")).thenReturn(4L);
    when(requestRepository.countByStatus("ACCEPTED")).thenReturn(3L);
    when(requestRepository.countByStatus("REJECTED")).thenReturn(3L);

    RequestStats stats = requestService.getRequestStats();

    assertEquals(10, stats.total());
    assertEquals(4, stats.pending());
    assertEquals(3, stats.approved());
    assertEquals(3, stats.rejected());
  }

}
