package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Request;
import edu.dosw.model.enums.Role;
import edu.dosw.model.enums.Status;
import edu.dosw.services.RequestService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class RequestControllerTest {

  @Mock private RequestService requestService;

  @InjectMocks private RequestController requestController;

  private Request request;
  private RequestDTO requestDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    request = new Request("student123", "Need schedule change", "TRANSFER", "G1", "G2");
    request.setRequestId(UUID.randomUUID().toString());
    request.setStatus(Status.PENDING);
    request.setCreatedAt(LocalDateTime.now());

    requestDTO =
        new RequestDTO(
            request.getRequestId(),
            request.getStudentId(),
            request.getType(),
            request.getIsExceptional(),
            request.getStatus(),
            request.getDescription(),
            request.getOriginGroupId(),
            request.getDestinationGroupId(),
            request.getAnswer(),
            request.getGestedBy());
  }

  @Test
  void createRequest_ShouldReturnCreatedRequest() {
    when(requestService.createRequest(requestDTO)).thenReturn(request);

    ResponseEntity<Request> response = requestController.createRequest(requestDTO);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(request, response.getBody());
    verify(requestService, times(1)).createRequest(requestDTO);
  }

  @Test
  void fetchRequests_ShouldReturnListOfRequests() {
    when(requestService.fetchRequests(Role.STUDENT, "student123")).thenReturn(List.of(request));

    ResponseEntity<List<Request>> response =
        requestController.fetchRequests("student123", Role.STUDENT);

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("student123", response.getBody().get(0).getStudentId());
  }

  @Test
  void updateRequestStatus_ShouldReturnUpdatedRequest() {
    request.setStatus(Status.ACCEPTED);
    when(requestService.updateRequestStatus("123", Status.ACCEPTED)).thenReturn(request);

    ResponseEntity<Request> response =
        requestController.updateRequestStatus("123", Status.ACCEPTED);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(Status.ACCEPTED, response.getBody().getStatus());
    verify(requestService, times(1)).updateRequestStatus("123", Status.ACCEPTED);
  }

  @Test
  void getRequestStats_ShouldReturnStats() {
    RequestStats stats = new RequestStats(5, 0, 2, 3);
    when(requestService.getRequestStats()).thenReturn(stats);

    ResponseEntity<RequestStats> response = requestController.getRequestStats();

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(stats, response.getBody());
  }

  @Test
  void deleteRequest_ShouldCallServiceAndReturnNoContent() {
    ResponseEntity<Void> response = requestController.deleteRequest("123");

    assertEquals(204, response.getStatusCodeValue());
    verify(requestService, times(1)).updateRequestStatus("123", Status.CANCELLED);
  }

  @Test
  void respondToRequest_ShouldReturnResponseWhenFound() {
    Request responseRequest = new Request("student123", "Response OK", "TRANSFER", "G1", "G2");
    when(requestService.respondToRequest(eq("123"), any(Request.class)))
        .thenReturn(responseRequest);

    ResponseEntity<Request> response = requestController.respondToRequest("123", responseRequest);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("student123", response.getBody().getStudentId());
  }

  @Test
  void respondToRequest_ShouldReturnNullWhenNotFound() {
    when(requestService.respondToRequest(eq("123"), any(Request.class))).thenReturn(null);

    ResponseEntity<Request> response = requestController.respondToRequest("123", request);

    assertNull(response);
  }
}
