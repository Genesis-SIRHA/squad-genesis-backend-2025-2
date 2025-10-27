package edu.dosw.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.ReportDTO;
import edu.dosw.model.Request;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import edu.dosw.model.enums.Role;
import edu.dosw.services.RequestService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

  @Mock private RequestService requestService;

  @InjectMocks private RequestController requestController;



  @Test
  void createRequest_WithJoinType_ShouldReturnCreatedRequest() {
    CreateRequestDto createDto =
        new CreateRequestDto(
            "STU001", RequestType.JOIN, "Request to join math group", null, "GROUP001");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description("Request to join math group")
            .destinationGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("STU001", response.getBody().getStudentId());
    assertEquals(RequestType.JOIN, response.getBody().getType());
    assertEquals(RequestStatus.PENDING, response.getBody().getStatus());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_WithSwapType_ShouldReturnCreatedRequest() {
    CreateRequestDto createDto =
        new CreateRequestDto(
            "STU001", RequestType.SWAP, "Request to swap groups", "GROUP001", "GROUP002");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.SWAP)
            .description("Request to swap groups")
            .originGroupId("GROUP001")
            .destinationGroupId("GROUP002")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(RequestType.SWAP, response.getBody().getType());
    assertEquals("GROUP001", response.getBody().getOriginGroupId());
    assertEquals("GROUP002", response.getBody().getDestinationGroupId());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_WithCancellationType_ShouldReturnCreatedRequest() {
    CreateRequestDto createDto =
        new CreateRequestDto(
            "STU001", RequestType.CANCELLATION, "Request to cancel course", "GROUP001", null);
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.CANCELLATION)
            .description("Request to cancel course")
            .originGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(RequestType.CANCELLATION, response.getBody().getType());
    assertEquals("GROUP001", response.getBody().getOriginGroupId());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_WithNullDescription_ShouldReturnCreatedRequest() {
    CreateRequestDto createDto =
        new CreateRequestDto("STU001", RequestType.JOIN, null, null, "GROUP001");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .destinationGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNull(response.getBody().getDescription());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_WithEmptyDescription_ShouldReturnCreatedRequest() {
    CreateRequestDto createDto =
        new CreateRequestDto("STU001", RequestType.JOIN, "", null, "GROUP001");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description("")
            .destinationGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("", response.getBody().getDescription());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_WithLongDescription_ShouldReturnCreatedRequest() {
    String longDescription = "A".repeat(500);
    CreateRequestDto createDto =
        new CreateRequestDto("STU001", RequestType.JOIN, longDescription, null, "GROUP001");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description(longDescription)
            .destinationGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(longDescription, response.getBody().getDescription());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_WithSpecialCharactersInDescription_ShouldReturnCreatedRequest() {
    CreateRequestDto createDto =
        new CreateRequestDto(
            "STU001", RequestType.JOIN, "Request with spécial chàrâctérs!", null, "GROUP001");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description("Request with spécial chàrâctérs!")
            .destinationGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Request with spécial chàrâctérs!", response.getBody().getDescription());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_WithDifferentStudentIds_ShouldReturnCreatedRequests() {
    CreateRequestDto createDto1 =
        new CreateRequestDto("STU001", RequestType.JOIN, "Request 1", null, "GROUP001");
    CreateRequestDto createDto2 =
        new CreateRequestDto("STU002", RequestType.JOIN, "Request 2", null, "GROUP002");

    Request createdRequest1 =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description("Request 1")
            .destinationGroupId("GROUP001")
            .build();
    Request createdRequest2 =
        new Request.RequestBuilder()
            .studentId("STU002")
            .type(RequestType.JOIN)
            .description("Request 2")
            .destinationGroupId("GROUP002")
            .build();

    when(requestService.createRequest(createDto1)).thenReturn(createdRequest1);
    when(requestService.createRequest(createDto2)).thenReturn(createdRequest2);

    ResponseEntity<Request> response1 = requestController.createRequest(createDto1);
    ResponseEntity<Request> response2 = requestController.createRequest(createDto2);

    assertEquals(HttpStatus.OK, response1.getStatusCode());
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    assertEquals("STU001", response1.getBody().getStudentId());
    assertEquals("STU002", response2.getBody().getStudentId());
    verify(requestService, times(1)).createRequest(createDto1);
    verify(requestService, times(1)).createRequest(createDto2);
  }

  @Test
  void createRequest_WhenServiceThrowsException_ShouldPropagateException() {
    CreateRequestDto createDto =
        new CreateRequestDto("STU001", RequestType.JOIN, "Request", null, "GROUP001");
    when(requestService.createRequest(createDto)).thenThrow(new RuntimeException("Database error"));

    assertThrows(RuntimeException.class, () -> requestController.createRequest(createDto));
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_VerifyAutoGeneratedFields() {
    CreateRequestDto createDto =
        new CreateRequestDto("STU001", RequestType.JOIN, "Request", null, "GROUP001");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description("Request")
            .destinationGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    ResponseEntity<Request> response = requestController.createRequest(createDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody().getRequestId());
    assertNotNull(response.getBody().getCreatedAt());
    assertNotNull(response.getBody().getUpdatedAt());
    assertEquals(RequestStatus.PENDING, response.getBody().getStatus());
    assertFalse(response.getBody().getIsExceptional());
    verify(requestService, times(1)).createRequest(createDto);
  }

  @Test
  void createRequest_ShouldCallServiceExactlyOnce() {
    CreateRequestDto createDto =
        new CreateRequestDto("STU001", RequestType.JOIN, "Request", null, "GROUP001");
    Request createdRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description("Request")
            .destinationGroupId("GROUP001")
            .build();

    when(requestService.createRequest(createDto)).thenReturn(createdRequest);

    requestController.createRequest(createDto);

    verify(requestService, times(1)).createRequest(createDto);
    verifyNoMoreInteractions(requestService);
  }

  @Test
  void fetchRequests_ByStudentRole_ShouldReturnStudentRequests() {
    String userId = "STU001";
    Role role = Role.STUDENT;
    List<Request> expectedRequests =
        Arrays.asList(
            new Request.RequestBuilder()
                .studentId("STU001")
                .type(RequestType.JOIN)
                .description("Join request")
                .build(),
            new Request.RequestBuilder()
                .studentId("STU001")
                .type(RequestType.CANCELLATION)
                .description("Cancel course")
                .build());

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals("STU001", response.getBody().get(0).getStudentId());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_ByProfessorRole_ShouldReturnProfessorRequests() {
    String userId = "PROF001";
    Role role = Role.PROFESSOR;
    List<Request> expectedRequests =
        List.of(
            new Request.RequestBuilder()
                .studentId("STU001")
                .type(RequestType.JOIN)
                .description("Join request for professor's group")
                .build());

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_ByDeanRole_ShouldReturnDeanRequests() {
    String userId = "DEAN001";
    Role role = Role.DEAN;
    List<Request> expectedRequests =
        Arrays.asList(
            new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build(),
            new Request.RequestBuilder().studentId("STU002").type(RequestType.SWAP).build());

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_ByAdministratorRole_ShouldReturnAllRequests() {
    String userId = "ADMIN001";
    Role role = Role.ADMINISTRATOR;
    List<Request> expectedRequests =
        Arrays.asList(
            new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build(),
            new Request.RequestBuilder().studentId("STU002").type(RequestType.SWAP).build(),
            new Request.RequestBuilder()
                .studentId("STU003")
                .type(RequestType.CANCELLATION)
                .build());

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(3, response.getBody().size());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_WithEmptyResult_ShouldReturnEmptyList() {
    String userId = "STU999";
    Role role = Role.STUDENT;
    List<Request> expectedRequests = Collections.emptyList();

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_WithNullResult_ShouldReturnNull() {
    String userId = "STU999";
    Role role = Role.STUDENT;

    when(requestService.fetchRequests(role, userId)).thenReturn(null);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_WhenServiceThrowsException_ShouldPropagateException() {
    String userId = "STU001";
    Role role = Role.STUDENT;
    when(requestService.fetchRequests(role, userId))
        .thenThrow(new RuntimeException("Service error"));

    assertThrows(RuntimeException.class, () -> requestController.fetchRequests(userId, role));
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_WithDifferentUserIds_ShouldReturnDifferentResults() {
    String userId1 = "STU001";
    String userId2 = "STU002";
    Role role = Role.STUDENT;

    List<Request> requests1 =
        List.of(new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build());
    List<Request> requests2 =
        List.of(
            new Request.RequestBuilder()
                .studentId("STU002")
                .type(RequestType.CANCELLATION)
                .build());

    when(requestService.fetchRequests(role, userId1)).thenReturn(requests1);
    when(requestService.fetchRequests(role, userId2)).thenReturn(requests2);

    ResponseEntity<List<Request>> response1 = requestController.fetchRequests(userId1, role);
    ResponseEntity<List<Request>> response2 = requestController.fetchRequests(userId2, role);

    assertEquals(HttpStatus.OK, response1.getStatusCode());
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    assertEquals("STU001", response1.getBody().get(0).getStudentId());
    assertEquals("STU002", response2.getBody().get(0).getStudentId());
    verify(requestService, times(1)).fetchRequests(role, userId1);
    verify(requestService, times(1)).fetchRequests(role, userId2);
  }

  @Test
  void fetchRequests_WithSpecialCharactersInUserId_ShouldHandleCorrectly() {
    String userId = "STU-001-ABC";
    Role role = Role.STUDENT;
    List<Request> expectedRequests =
        List.of(
            new Request.RequestBuilder().studentId("STU-001-ABC").type(RequestType.JOIN).build());

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("STU-001-ABC", response.getBody().get(0).getStudentId());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_WithLongUserId_ShouldHandleCorrectly() {
    String userId = "STU001234567890123456789";
    Role role = Role.STUDENT;
    List<Request> expectedRequests =
        List.of(
            new Request.RequestBuilder()
                .studentId("STU001234567890123456789")
                .type(RequestType.JOIN)
                .build());

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchRequests(userId, role);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("STU001234567890123456789", response.getBody().get(0).getStudentId());
    verify(requestService, times(1)).fetchRequests(role, userId);
  }

  @Test
  void fetchRequests_VerifyRoleSpecificLogic() {
    String userId = "PROF001";
    Role role = Role.PROFESSOR;
    List<Request> expectedRequests =
        List.of(new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build());

    when(requestService.fetchRequests(role, userId)).thenReturn(expectedRequests);

    requestController.fetchRequests(userId, role);

    verify(requestService, times(1)).fetchRequests(role, userId);
    verify(requestService, never()).fetchRequests(Role.STUDENT, userId);
    verify(requestService, never()).fetchRequests(Role.DEAN, userId);
  }

  @Test
  void fetchAllRequests_WithEmptyDatabase_ShouldReturnEmptyList() {
    List<Request> expectedRequests = Collections.emptyList();

    when(requestService.fetchAllRequests()).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchAllRequests();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(requestService, times(1)).fetchAllRequests();
  }

  @Test
  void fetchAllRequests_WithLargeDataset_ShouldHandlePerformance() {
    List<Request> largeList =
        Arrays.asList(
            new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build(),
            new Request.RequestBuilder().studentId("STU002").type(RequestType.JOIN).build(),
            new Request.RequestBuilder().studentId("STU003").type(RequestType.JOIN).build(),
            new Request.RequestBuilder().studentId("STU004").type(RequestType.JOIN).build(),
            new Request.RequestBuilder().studentId("STU005").type(RequestType.JOIN).build());

    when(requestService.fetchAllRequests()).thenReturn(largeList);

    ResponseEntity<List<Request>> response = requestController.fetchAllRequests();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(5, response.getBody().size());
    verify(requestService, times(1)).fetchAllRequests();
  }

  @Test
  void fetchAllRequests_WhenServiceThrowsException_ShouldPropagateException() {
    when(requestService.fetchAllRequests()).thenThrow(new RuntimeException("Database error"));

    assertThrows(RuntimeException.class, () -> requestController.fetchAllRequests());
    verify(requestService, times(1)).fetchAllRequests();
  }

  @Test
  void fetchAllRequests_WithNullResult_ShouldReturnNull() {
    when(requestService.fetchAllRequests()).thenReturn(null);

    ResponseEntity<List<Request>> response = requestController.fetchAllRequests();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody());
    verify(requestService, times(1)).fetchAllRequests();
  }

  @Test
  void fetchAllRequests_ShouldReturnCorrectResponseType() {
    List<Request> expectedRequests =
        List.of(new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build());

    when(requestService.fetchAllRequests()).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.fetchAllRequests();

    assertInstanceOf(ResponseEntity.class, response);
    assertInstanceOf(List.class, response.getBody());
    assertEquals(Request.class, response.getBody().get(0).getClass());
  }

  @Test
  void getRequest_ById_ShouldReturnRequest() {
    String requestId = "REQ001";
    Request expectedRequest =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .description("Test request")
            .build();

    when(requestService.getRequest(requestId)).thenReturn(expectedRequest);

    ResponseEntity<Request> response = requestController.getRequest(requestId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("STU001", response.getBody().getStudentId());
    verify(requestService, times(1)).getRequest(requestId);
  }

  @Test
  void getRequest_WithNonExistentId_ShouldThrowException() {
    String requestId = "NONEXISTENT";
    when(requestService.getRequest(requestId)).thenThrow(new RuntimeException("Request not found"));

    assertThrows(RuntimeException.class, () -> requestController.getRequest(requestId));
    verify(requestService, times(1)).getRequest(requestId);
  }

  @Test
  void getRequest_WithDifferentStatuses_ShouldReturnRequest() {
    String requestId = "REQ002";
    Request expectedRequest =
        new Request.RequestBuilder().studentId("STU002").type(RequestType.JOIN).build();
    expectedRequest.setStatus(RequestStatus.ACCEPTED);

    when(requestService.getRequest(requestId)).thenReturn(expectedRequest);

    ResponseEntity<Request> response = requestController.getRequest(requestId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(RequestStatus.ACCEPTED, response.getBody().getStatus());
    verify(requestService, times(1)).getRequest(requestId);
  }

  @Test
  void getRequest_WithSpecialRequestId_ShouldHandleCorrectly() {
    String requestId = "REQ-001-ABC";
    Request expectedRequest =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build();

    when(requestService.getRequest(requestId)).thenReturn(expectedRequest);

    ResponseEntity<Request> response = requestController.getRequest(requestId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(requestService, times(1)).getRequest(requestId);
  }

  @Test
  void getRequest_WithLongRequestId_ShouldHandleCorrectly() {
    String requestId = "REQ001234567890123456789";
    Request expectedRequest =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build();

    when(requestService.getRequest(requestId)).thenReturn(expectedRequest);

    ResponseEntity<Request> response = requestController.getRequest(requestId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(requestService, times(1)).getRequest(requestId);
  }

  @Test
  void getStudentHistorialRequests_ShouldReturnStudentRequests() {
    String studentId = "STU001";
    List<Request> expectedRequests =
        Arrays.asList(
            new Request.RequestBuilder().studentId(studentId).type(RequestType.JOIN).build(),
            new Request.RequestBuilder()
                .studentId(studentId)
                .type(RequestType.CANCELLATION)
                .build());

    when(requestService.fetchRequests(Role.STUDENT, studentId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response =
        requestController.getStudentHistorialRequests(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertEquals(studentId, response.getBody().get(0).getStudentId());
    verify(requestService, times(1)).fetchRequests(Role.STUDENT, studentId);
  }

  @Test
  void getStudentHistorialRequests_WithNoRequests_ShouldReturnEmptyList() {
    String studentId = "STU999";
    List<Request> expectedRequests = Collections.emptyList();

    when(requestService.fetchRequests(Role.STUDENT, studentId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response =
        requestController.getStudentHistorialRequests(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(requestService, times(1)).fetchRequests(Role.STUDENT, studentId);
  }

  @Test
  void getStudentHistorialRequests_WithMultipleRequestTypes_ShouldReturnAll() {
    String studentId = "STU001";
    List<Request> expectedRequests =
        Arrays.asList(
            new Request.RequestBuilder().studentId(studentId).type(RequestType.JOIN).build(),
            new Request.RequestBuilder().studentId(studentId).type(RequestType.SWAP).build(),
            new Request.RequestBuilder()
                .studentId(studentId)
                .type(RequestType.CANCELLATION)
                .build());

    when(requestService.fetchRequests(Role.STUDENT, studentId)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response =
        requestController.getStudentHistorialRequests(studentId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(3, response.getBody().size());
    verify(requestService, times(1)).fetchRequests(Role.STUDENT, studentId);
  }

  @Test
  void getStudentHistorialRequests_WhenServiceThrowsException_ShouldPropagateException() {
    String studentId = "STU001";
    when(requestService.fetchRequests(Role.STUDENT, studentId))
        .thenThrow(new RuntimeException("Service error"));

    assertThrows(
        RuntimeException.class, () -> requestController.getStudentHistorialRequests(studentId));
    verify(requestService, times(1)).fetchRequests(Role.STUDENT, studentId);
  }

  @Test
  void getStudentHistorialRequests_WithDifferentStudents_ShouldReturnDifferentResults() {
    String studentId1 = "STU001";
    String studentId2 = "STU002";

    List<Request> requests1 =
        List.of(new Request.RequestBuilder().studentId(studentId1).type(RequestType.JOIN).build());
    List<Request> requests2 =
        List.of(
            new Request.RequestBuilder()
                .studentId(studentId2)
                .type(RequestType.CANCELLATION)
                .build());

    when(requestService.fetchRequests(Role.STUDENT, studentId1)).thenReturn(requests1);
    when(requestService.fetchRequests(Role.STUDENT, studentId2)).thenReturn(requests2);

    ResponseEntity<List<Request>> response1 =
        requestController.getStudentHistorialRequests(studentId1);
    ResponseEntity<List<Request>> response2 =
        requestController.getStudentHistorialRequests(studentId2);

    assertEquals(HttpStatus.OK, response1.getStatusCode());
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    assertEquals(studentId1, response1.getBody().get(0).getStudentId());
    assertEquals(studentId2, response2.getBody().get(0).getStudentId());
    verify(requestService, times(1)).fetchRequests(Role.STUDENT, studentId1);
    verify(requestService, times(1)).fetchRequests(Role.STUDENT, studentId2);
  }

  @Test
  void getStudentHistorialRequests_ShouldAlwaysUseStudentRole() {
    String studentId = "STU001";
    List<Request> expectedRequests =
        List.of(new Request.RequestBuilder().studentId(studentId).type(RequestType.JOIN).build());

    when(requestService.fetchRequests(Role.STUDENT, studentId)).thenReturn(expectedRequests);

    requestController.getStudentHistorialRequests(studentId);

    verify(requestService, times(1)).fetchRequests(Role.STUDENT, studentId);
    verify(requestService, never()).fetchRequests(Role.PROFESSOR, studentId);
    verify(requestService, never()).fetchRequests(Role.DEAN, studentId);
    verify(requestService, never()).fetchRequests(Role.ADMINISTRATOR, studentId);
  }

  @Test
  void getRequestByFacultyName_ShouldReturnFacultyRequests() {
    String facultyName = "engineering";
    List<Request> expectedRequests =
        List.of(new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build());

    when(requestService.fetchRequestsByFacultyName(facultyName)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.getRequestByFacultyName(facultyName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    verify(requestService, times(1)).fetchRequestsByFacultyName(facultyName);
  }

  @Test
  void getRequestByFacultyName_WithUpperCaseInput_ShouldConvertToLowerCase() {
    String facultyName = "ENGINEERING";
    List<Request> expectedRequests =
        List.of(new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build());

    when(requestService.fetchRequestsByFacultyName("engineering")).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.getRequestByFacultyName(facultyName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(requestService, times(1)).fetchRequestsByFacultyName("engineering");
  }

  @Test
  void getRequestByFacultyName_WithMixedCaseInput_ShouldConvertToLowerCase() {
    String facultyName = "EnGiNeErInG";
    List<Request> expectedRequests =
        List.of(new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build());

    when(requestService.fetchRequestsByFacultyName("engineering")).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.getRequestByFacultyName(facultyName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(requestService, times(1)).fetchRequestsByFacultyName("engineering");
  }

  @Test
  void getRequestByFacultyName_WithEmptyResult_ShouldReturnEmptyList() {
    String facultyName = "nonexistent";
    List<Request> expectedRequests = Collections.emptyList();

    when(requestService.fetchRequestsByFacultyName(facultyName)).thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.getRequestByFacultyName(facultyName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
    verify(requestService, times(1)).fetchRequestsByFacultyName(facultyName);
  }

  @Test
  void getRequestByFacultyName_WhenServiceThrowsException_ShouldPropagateException() {
    String facultyName = "engineering";
    when(requestService.fetchRequestsByFacultyName(facultyName))
        .thenThrow(new RuntimeException("Service error"));

    assertThrows(
        RuntimeException.class, () -> requestController.getRequestByFacultyName(facultyName));
    verify(requestService, times(1)).fetchRequestsByFacultyName(facultyName);
  }

  @Test
  void getRequestByFacultyName_WithSpecialCharacters_ShouldHandleCorrectly() {
    String facultyName = "computer-science";
    List<Request> expectedRequests =
        List.of(new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build());

    when(requestService.fetchRequestsByFacultyName("computer-science"))
        .thenReturn(expectedRequests);

    ResponseEntity<List<Request>> response = requestController.getRequestByFacultyName(facultyName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    verify(requestService, times(1)).fetchRequestsByFacultyName("computer-science");
  }
}
