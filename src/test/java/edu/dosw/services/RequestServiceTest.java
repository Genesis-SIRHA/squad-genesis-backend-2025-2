package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestStats;
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
  private AdministrativeService administrativeService;

  @BeforeEach
  void setUp() {
    requestRepository = mock(RequestRepository.class);
    facultyRepository = mock(FacultyRepository.class);
    groupRepository = mock(GroupRepository.class);
    administrativeService = mock(AdministrativeService.class);
    requestService =
        new RequestService(requestRepository, facultyRepository, groupRepository, administrativeService);
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

  @Test
  void createRequest_shouldCreateRequestWhenGroupsExist() {
    RequestDTO dto =
        new RequestDTO(
            "1",
            "student1",
            "TYPE_A",
            null,
            null,
            "desc",
            "originGroup",
            "destGroup",
            "answer",
            "admin");

    Group origin = new Group();
    origin.setGroupCode("originGroup");
    Group dest = new Group();
    dest.setGroupCode("destGroup");

    when(groupRepository.findByGroupCode("originGroup")).thenReturn(origin);
    when(groupRepository.findByGroupCode("destGroup")).thenReturn(dest);
    when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));

    Request result = requestService.createRequest(dto);

    assertNotNull(result);
    assertEquals("student1", result.getStudentId());
    assertEquals("originGroup", result.getOriginGroupId());
    assertEquals("destGroup", result.getDestinationGroupId());
    assertEquals(Status.PENDING, result.getStatus());
    assertFalse(result.getIsExceptional());
  }

  @Test
  void createRequest_shouldThrowWhenOriginGroupNotFound() {
    RequestDTO dto =
        new RequestDTO(
            "1",
            "student1",
            "TYPE_A",
            false,
            Status.PENDING,
            "desc",
            "invalidGroup",
            "destGroup",
            "answer",
            "admin");

    when(groupRepository.findByGroupCode("invalidGroup")).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(dto));
  }

  @Test
  void createRequest_shouldThrowWhenDestinationGroupNotFound() {
    RequestDTO dto =
        new RequestDTO(
            "1",
            "student1",
            "TYPE_A",
            false,
            Status.PENDING,
            "desc",
            "originGroup",
            "invalidGroup",
            "answer",
            "admin");

    Group origin = new Group();
    origin.setGroupCode("originGroup");

    when(groupRepository.findByGroupCode("originGroup")).thenReturn(origin);
    when(groupRepository.findByGroupCode("invalidGroup")).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(dto));
  }

  @Test
  void updateRequestStatus_shouldUpdateStatus() {
    Request request = new Request();
    request.setStatus(Status.PENDING);

    when(requestRepository.findById("123")).thenReturn(Optional.of(request));
    when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));

    Request result = requestService.updateRequestStatus("123", Status.ACCEPTED);

    assertEquals(Status.ACCEPTED, result.getStatus());
  }

  @Test
  void updateRequestStatus_shouldThrowWhenNotFound() {
    when(requestRepository.findById("123")).thenReturn(Optional.empty());

    assertThrows(
        RuntimeException.class, () -> requestService.updateRequestStatus("123", Status.ACCEPTED));
  }

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

  @Test
  void respondToRequest_shouldUpdateAndSave() {
    Request existing = new Request();
    existing.setRequestId("1");
    existing.setStatus(Status.PENDING);

    Request response = new Request();
    response.setStatus(Status.ACCEPTED);
    response.setAnswer("OK");
    response.setGestedBy("admin");

    when(requestRepository.findById("1")).thenReturn(Optional.of(existing));
    when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));

    Request result = requestService.respondToRequest("1", response);

    assertNotNull(result);
    assertEquals(Status.ACCEPTED, result.getStatus());
    assertEquals("OK", result.getAnswer());
    assertEquals("admin", result.getGestedBy());
    assertEquals(LocalDate.now(), result.getAnswerAt());
  }

  @Test
  void respondToRequest_shouldThrowForNullStatus() {
    Request existing = new Request();
    existing.setRequestId("1");
    existing.setStatus(Status.PENDING);

    Request response = new Request();
    response.setStatus(null);

    when(requestRepository.findById("1")).thenReturn(Optional.of(existing));

    assertThrows(
        IllegalArgumentException.class, () -> requestService.respondToRequest("1", response));
  }

  @Test
  void respondToRequest_shouldReturnNullWhenNotFound() {
    when(requestRepository.findById("1")).thenReturn(Optional.empty());

    Request response = new Request();
    response.setStatus(Status.ACCEPTED);

    Request result = requestService.respondToRequest("1", response);

    assertNull(result);
  }
}
