package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.RequestPeriodDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.dto.UpdateRequestDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Request;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.UserServices.DeanService;
import edu.dosw.services.UserServices.ProfessorService;
import edu.dosw.services.UserServices.StudentService;
import edu.dosw.services.Validators.RequestValidator;
import edu.dosw.services.strategy.AnswerStrategies.AnswerStrategy;
import edu.dosw.services.strategy.AnswerStrategies.AnswerStrategyFactory;
import edu.dosw.services.strategy.queryStrategies.QueryStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

  @Mock private RequestRepository requestRepository;

  @Mock private RequestValidator requestValidator;

  @Mock private AuthenticationService authenticationService;

  @Mock private StudentService studentService;

  @Mock private DeanService deanService;

  @Mock private ProfessorService professorService;

  @Mock private AnswerStrategyFactory answerStrategyFactory;

  @Mock private AnswerStrategy answerStrategy;

  @Mock private RequestPeriodService requestPeriodService;

  @InjectMocks private RequestService requestService;

  private Request mockRequest1;
  private Request mockRequest2;
  private CreateRequestDto createRequestDto;
  private UpdateRequestDto updateRequestDto;

  private RequestPeriodDTO activePeriod;

  @BeforeEach
  void setUp() {
    // Setup mock request period with dates that will pass the validation
    LocalDate now = LocalDate.now();
    activePeriod =
        new RequestPeriodDTO(
            "PERIOD001",
            now.minusDays(1), // Start date is yesterday
            now.plusDays(30), // End date is 30 days from now
            "2024",
            "1",
            true);

    mockRequest1 =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.SWAP)
            .description("Request to swap group")
            .originGroupId("GROUP001")
            .destinationGroupId("GROUP002")
            .build();
    mockRequest1.setRequestId("REQ001");
    mockRequest1.setStatus(RequestStatus.PENDING);
    mockRequest1.setCreatedAt(now);

    mockRequest2 =
        new Request.RequestBuilder()
            .studentId("STU002")
            .type(RequestType.CANCELLATION)
            .description("Request to cancel enrollment")
            .originGroupId("GROUP003")
            .build();
    mockRequest2.setRequestId("REQ002");
    mockRequest2.setStatus(RequestStatus.ACCEPTED);
    mockRequest2.setCreatedAt(now.minusDays(5));

    createRequestDto =
        new CreateRequestDto(
            "STU001", RequestType.SWAP, "Request description", "GROUP001", "GROUP002");

    updateRequestDto =
        new UpdateRequestDto("REQ001", RequestStatus.ACCEPTED, "Request approved", "DEAN001");
  }

  @Test
  void fetchRequests_ShouldThrowBusinessException_WhenUnsupportedRole() {
    assertThrows(
        BusinessException.class, () -> requestService.fetchRequests(Role.ADMINISTRATOR, "USER001"));
  }

  @Test
  void createRequest_ShouldSaveAndReturnRequest_WhenValidDto() {
    // Arrange
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    doNothing().when(requestValidator).validateCreateRequest(createRequestDto);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    // Act
    Request result = requestService.createRequest(createRequestDto);

    // Assert
    assertNotNull(result);
    verify(requestValidator).validateCreateRequest(createRequestDto);
    verify(requestRepository).save(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void createRequest_ShouldThrowBusinessException_WhenRepositoryFails() {
    // Arrange
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    doNothing().when(requestValidator).validateCreateRequest(createRequestDto);
    when(requestRepository.save(any(Request.class))).thenThrow(new RuntimeException("Save failed"));

    // Act & Assert
    assertThrows(BusinessException.class, () -> requestService.createRequest(createRequestDto));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void updateRequest_ShouldUpdateAndReturnRequest_WhenValid() {
    // Arrange
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    doNothing().when(answerStrategy).answerRequest(any(Request.class));
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    // Act
    Request result = requestService.updateRequest("USER001", updateRequestDto);

    // Assert
    assertNotNull(result);
    assertEquals(RequestStatus.ACCEPTED, result.getStatus());
    verify(requestRepository).findByRequestId("REQ001");
    verify(answerStrategy).answerRequest(any(Request.class));
    verify(requestRepository).save(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void updateRequest_ShouldNotCallAnswerStrategy_WhenStatusNotAccepted() {
    // Arrange
    UpdateRequestDto rejectedDto =
        new UpdateRequestDto("REQ001", RequestStatus.REJECTED, "Request rejected", "DEAN001");

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    // Act
    Request result = requestService.updateRequest("USER001", rejectedDto);

    // Assert
    assertNotNull(result);
    verify(answerStrategyFactory, never()).getStrategy(any(RequestType.class));
    verify(requestRepository).save(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void deleteRequestStatus_ShouldThrowResourceNotFoundException_WhenNotFound() {
    when(requestRepository.findByRequestId("REQ999")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> requestService.deleteRequestStatus("REQ999"));
    verify(requestRepository, never()).delete(any(Request.class));
  }

  @Test
  void deleteRequestStatus_ShouldThrowBusinessException_WhenDeleteFails() {
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doThrow(new RuntimeException("Delete failed")).when(requestRepository).delete(mockRequest1);

    assertThrows(BusinessException.class, () -> requestService.deleteRequestStatus("REQ001"));
  }

  @Test
  void getRequest_ShouldReturnRequest_WhenExists() {
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));

    Request result = requestService.getRequest("REQ001");

    assertNotNull(result);
    assertEquals("REQ001", result.getRequestId());
    verify(requestRepository).findByRequestId("REQ001");
  }

  @Test
  void getRequest_ShouldThrowResourceNotFoundException_WhenNotFound() {
    when(requestRepository.findByRequestId("REQ999")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> requestService.getRequest("REQ999"));
  }

  @Test
  void fetchRequestsByFacultyName_ShouldReturnFilteredRequests_WhenValid() {
    String facultyName = "Engineering";

    when(requestRepository.findAll()).thenReturn(Arrays.asList(mockRequest1, mockRequest2));
    when(studentService.getFacultyByStudentId("STU001")).thenReturn("Engineering");
    when(studentService.getFacultyByStudentId("STU002")).thenReturn("Medicine");
    doNothing().when(requestValidator).validateFacultyName(facultyName);

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("STU001", result.get(0).getStudentId());
    verify(requestValidator).validateFacultyName(facultyName);
    verify(requestRepository).findAll();
  }

  @Test
  void fetchRequestsByFacultyName_ShouldReturnEmptyList_WhenNoMatches() {
    String facultyName = "Engineering";

    when(requestRepository.findAll()).thenReturn(Arrays.asList(mockRequest1, mockRequest2));
    when(studentService.getFacultyByStudentId(anyString())).thenReturn("Medicine");
    doNothing().when(requestValidator).validateFacultyName(facultyName);

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void updateRequest_ShouldUpdateManagedBy_WhenProvided() {

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    requestService.updateRequest("USER001", updateRequestDto);

    verify(requestRepository).save(argThat(request -> request.getGestedBy().equals("DEAN001")));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void updateRequest_ShouldUpdateAnswer_WhenProvided() {

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    requestService.updateRequest("USER001", updateRequestDto);

    verify(requestRepository)
        .save(argThat(request -> request.getAnswer().equals("Request approved")));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void createRequest_ShouldBuildRequestWithCorrectFields() {
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    doNothing().when(requestValidator).validateCreateRequest(createRequestDto);
    when(requestRepository.save(any(Request.class)))
        .thenAnswer(
            invocation -> {
              Request saved = invocation.getArgument(0);
              assertEquals("STU001", saved.getStudentId());
              assertEquals(RequestType.SWAP, saved.getType());
              assertEquals("Request description", saved.getDescription());
              assertEquals("GROUP001", saved.getOriginGroupId());
              assertEquals("GROUP002", saved.getDestinationGroupId());
              assertEquals(RequestStatus.PENDING, saved.getStatus());
              assertNotNull(saved.getRequestId());
              assertNotNull(saved.getCreatedAt());
              return saved;
            });

    requestService.createRequest(createRequestDto);
    verify(requestRepository).save(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void updateRequest_ShouldHandleJoinRequestType() {

    Request joinRequest =
        new Request.RequestBuilder()
            .studentId("STU003")
            .type(RequestType.JOIN)
            .description("Request to join group")
            .destinationGroupId("GROUP005")
            .build();
    joinRequest.setRequestId("REQ003");
    joinRequest.setStatus(RequestStatus.PENDING);

    UpdateRequestDto joinUpdateDto =
        new UpdateRequestDto("REQ003", RequestStatus.ACCEPTED, "Join request approved", "PROF001");

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ003")).thenReturn(Optional.of(joinRequest));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.JOIN)).thenReturn(answerStrategy);
    doNothing().when(answerStrategy).answerRequest(any(Request.class));
    when(requestRepository.save(any(Request.class))).thenReturn(joinRequest);

    Request result = requestService.updateRequest("USER001", joinUpdateDto);

    assertNotNull(result);
    verify(answerStrategyFactory).getStrategy(RequestType.JOIN);
    verify(answerStrategy).answerRequest(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void updateRequest_ShouldHandleCancellationRequestType() {

    Request cancellationRequest =
        new Request.RequestBuilder()
            .studentId("STU004")
            .type(RequestType.CANCELLATION)
            .description("Request to cancel")
            .originGroupId("GROUP006")
            .build();
    cancellationRequest.setRequestId("REQ004");
    cancellationRequest.setStatus(RequestStatus.PENDING);

    UpdateRequestDto cancellationUpdateDto =
        new UpdateRequestDto("REQ004", RequestStatus.ACCEPTED, "Cancellation approved", "DEAN002");

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ004")).thenReturn(Optional.of(cancellationRequest));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.CANCELLATION)).thenReturn(answerStrategy);
    doNothing().when(answerStrategy).answerRequest(any(Request.class));
    when(requestRepository.save(any(Request.class))).thenReturn(cancellationRequest);

    Request result = requestService.updateRequest("USER001", cancellationUpdateDto);

    assertNotNull(result);
    verify(answerStrategyFactory).getStrategy(RequestType.CANCELLATION);
    verify(answerStrategy).answerRequest(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void createRequest_ShouldHandleJoinRequestType() {

    CreateRequestDto joinRequestDto =
        new CreateRequestDto("STU005", RequestType.JOIN, "Request to join group", "GROUP003", null);

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    doNothing().when(requestValidator).validateCreateRequest(joinRequestDto);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    Request result = requestService.createRequest(joinRequestDto);

    assertNotNull(result);
    verify(requestValidator).validateCreateRequest(joinRequestDto);
    verify(requestRepository).save(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void createRequest_ShouldHandleCancellationRequestType() {

    CreateRequestDto cancellationRequestDto =
        new CreateRequestDto(
            "STU006", RequestType.CANCELLATION, "Request to cancel enrollment", null, "GROUP004");

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    doNothing().when(requestValidator).validateCreateRequest(cancellationRequestDto);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest2);

    Request result = requestService.createRequest(cancellationRequestDto);

    assertNotNull(result);
    verify(requestValidator).validateCreateRequest(cancellationRequestDto);
    verify(requestRepository).save(any(Request.class));
    verify(requestPeriodService).getActivePeriod();
  }

  @Test
  void requestShouldHaveDefaultValues_WhenCreated() {
    Request request =
        new Request.RequestBuilder()
            .studentId("STU007")
            .type(RequestType.SWAP)
            .description("Test request")
            .originGroupId("GROUP007")
            .destinationGroupId("GROUP008")
            .build();

    assertNotNull(request.getRequestId());
    assertNotNull(request.getCreatedAt());
    assertNotNull(request.getUpdatedAt());
    assertEquals(RequestStatus.PENDING, request.getStatus());
    assertFalse(request.getIsExceptional());
    assertNull(request.getGestedBy());
    assertNull(request.getAnswer());
  }

  @Test
  void countByGroupCodes_ShouldReturnCount() {
    List<String> groupCodes = List.of("GROUP001", "GROUP002");
    when(requestRepository.countByGroupCodes(groupCodes)).thenReturn(5);

    Integer result = requestService.countByGroupCodes(groupCodes);

    assertEquals(5, result);
    verify(requestRepository).countByGroupCodes(groupCodes);
  }

  @Test
  void countByGroupCodes_ShouldThrowBusinessException_WhenRepositoryFails() {
    List<String> groupCodes = List.of("GROUP001");
    when(requestRepository.countByGroupCodes(groupCodes))
        .thenThrow(new RuntimeException("DB error"));

    assertThrows(BusinessException.class, () -> requestService.countByGroupCodes(groupCodes));
  }

  @Test
  void getWaitingListOfGroup_ShouldReturnStudentIds() {
    String groupCode = "GROUP001";
    Request pendingRequest1 =
        new Request.RequestBuilder()
            .studentId("STU001")
            .type(RequestType.JOIN)
            .destinationGroupId(groupCode)
            .build();
    pendingRequest1.setStatus(RequestStatus.PENDING);

    Request pendingRequest2 =
        new Request.RequestBuilder()
            .studentId("STU002")
            .type(RequestType.JOIN)
            .destinationGroupId(groupCode)
            .build();
    pendingRequest2.setStatus(RequestStatus.PENDING);

    Request acceptedRequest =
        new Request.RequestBuilder()
            .studentId("STU003")
            .type(RequestType.JOIN)
            .destinationGroupId(groupCode)
            .build();
    acceptedRequest.setStatus(RequestStatus.ACCEPTED);

    when(requestRepository.getRequestByDestinationGroupId(groupCode))
        .thenReturn(List.of(pendingRequest1, pendingRequest2, acceptedRequest));

    List<String> result = requestService.getWaitingListOfGroup(groupCode);

    assertEquals(2, result.size());
    assertTrue(result.contains("STU001"));
    assertTrue(result.contains("STU002"));
    assertFalse(result.contains("STU003"));
  }

  @Test
  void getWaitingListOfGroup_ShouldThrowException_WhenNoRequestsFound() {
    String groupCode = "NONEXISTENT";
    when(requestRepository.getRequestByDestinationGroupId(groupCode)).thenReturn(null);

    assertThrows(RuntimeException.class, () -> requestService.getWaitingListOfGroup(groupCode));
  }

  @Test
  void countByGroupCodesAndStatus_ShouldReturnCount() {
    List<String> groupCodes = List.of("GROUP001", "GROUP002");
    RequestStatus status = RequestStatus.PENDING;
    when(requestRepository.countByGroupCodesAndStatus(groupCodes, status)).thenReturn(3);

    Integer result = requestService.countByGroupCodesAndStatus(groupCodes, status);

    assertEquals(3, result);
    verify(requestRepository).countByGroupCodesAndStatus(groupCodes, status);
  }

  @Test
  void countByGroupCodesAndType_ShouldReturnCount() {
    List<String> groupCodes = List.of("GROUP001");
    RequestType type = RequestType.JOIN;
    when(requestRepository.countByGroupCodesAndType(groupCodes, type)).thenReturn(2);

    Integer result = requestService.countByGroupCodesAndType(groupCodes, type);

    assertEquals(2, result);
    verify(requestRepository).countByGroupCodesAndType(groupCodes, type);
  }

  @Test
  void countByStatus_ShouldReturnCount() {
    RequestStatus status = RequestStatus.ACCEPTED;
    when(requestRepository.countByStatus(status)).thenReturn(25);

    Integer result = requestService.countByStatus(status);

    assertEquals(25, result);
    verify(requestRepository).countByStatus(status);
  }

  @Test
  void countByType_ShouldReturnCount() {
    RequestType type = RequestType.SWAP;
    when(requestRepository.countByType(type)).thenReturn(15);

    Integer result = requestService.countByType(type);

    assertEquals(15, result);
    verify(requestRepository).countByType(type);
  }

  @Test
  void countTotalRequests_ShouldReturnTotalCount() {
    when(requestRepository.count()).thenReturn(150L);

    Integer result = requestService.countTotalRequests();

    assertEquals(150, result);
    verify(requestRepository).count();
  }

  @Test
  void countTotalRequests_ShouldThrowBusinessException_WhenCountFails() {
    when(requestRepository.count()).thenThrow(new RuntimeException("Count failed"));

    assertThrows(BusinessException.class, () -> requestService.countTotalRequests());
  }

  @Test
  void requestAnswerReplicator_ShouldCallAnswerStrategy_WhenStatusAccepted() {
    Request request =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.SWAP).build();
    request.setStatus(RequestStatus.ACCEPTED);

    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);

    // Usar reflection para llamar al método privado
    invokePrivateMethod(requestService, "requestAnswerReplicator", request);

    verify(answerStrategy).answerRequest(request);
  }

  @Test
  void requestAnswerReplicator_ShouldNotCallAnswerStrategy_WhenStatusNotAccepted() {
    Request request =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.SWAP).build();
    request.setStatus(RequestStatus.PENDING);

    invokePrivateMethod(requestService, "requestAnswerReplicator", request);

    verify(answerStrategyFactory, never()).getStrategy(any());
    verify(answerStrategy, never()).answerRequest(any());
  }

  @Test
  void updateRequest_ShouldHandleWaitingStatus() {
    UpdateRequestDto waitingDto =
        new UpdateRequestDto("REQ001", RequestStatus.WAITING, "Need more info", "PROF001");
    Request request =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build();

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(request));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), eq(request), eq(waitingDto));
    when(requestRepository.save(any(Request.class))).thenReturn(request);

    Request result = requestService.updateRequest("USER001", waitingDto);

    assertNotNull(result);
    assertEquals(RequestStatus.WAITING, result.getStatus());
    verify(answerStrategyFactory, never()).getStrategy(any());
  }

  @Test
  void updateRequest_ShouldHandleInReviewStatus() {
    UpdateRequestDto inReviewDto =
        new UpdateRequestDto("REQ001", RequestStatus.IN_REVIEW, "Under review", "DEAN001");
    Request request =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.CANCELLATION).build();

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(request));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), eq(request), eq(inReviewDto));
    when(requestRepository.save(any(Request.class))).thenReturn(request);

    Request result = requestService.updateRequest("USER001", inReviewDto);

    assertNotNull(result);
    assertEquals(RequestStatus.IN_REVIEW, result.getStatus());
    verify(answerStrategyFactory, never()).getStrategy(any());
  }

  @Test
  void createRequest_ShouldSetDefaultValuesCorrectly() {
    CreateRequestDto createDto =
        new CreateRequestDto("STU001", RequestType.JOIN, "Test request", null, "GROUP001");

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    doNothing().when(requestValidator).validateCreateRequest(createDto);
    when(requestRepository.save(any(Request.class)))
        .thenAnswer(
            invocation -> {
              Request saved = invocation.getArgument(0);
              assertEquals("STU001", saved.getStudentId());
              assertEquals(RequestType.JOIN, saved.getType());
              assertEquals("Test request", saved.getDescription());
              assertEquals("GROUP001", saved.getDestinationGroupId());
              assertNull(saved.getOriginGroupId());
              assertEquals(RequestStatus.PENDING, saved.getStatus());
              assertNotNull(saved.getRequestId());
              assertNotNull(saved.getCreatedAt());
              assertNotNull(saved.getUpdatedAt());
              assertFalse(saved.getIsExceptional());
              assertNull(saved.getGestedBy());
              assertNull(saved.getAnswer());
              return saved;
            });

    requestService.createRequest(createDto);
  }

  @Test
  void updateRequest_ShouldHandleNullAnswerAndManagedBy() {
    UpdateRequestDto updateDto = new UpdateRequestDto("REQ001", RequestStatus.PENDING, null, null);
    Request request =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.SWAP).build();

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(request));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), eq(request), eq(updateDto));
    when(requestRepository.save(any(Request.class))).thenReturn(request);

    Request result = requestService.updateRequest("USER001", updateDto);

    assertNotNull(result);
    assertEquals(RequestStatus.PENDING, result.getStatus());
    assertNull(result.getAnswer());
    assertNull(result.getGestedBy());
  }

  @Test
  void updateRequest_ShouldUpdateOnlyStatusWhenOtherFieldsNull() {
    UpdateRequestDto updateDto = new UpdateRequestDto("REQ001", RequestStatus.REJECTED, null, null);
    Request originalRequest =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.CANCELLATION).build();
    originalRequest.setAnswer("Original answer");
    originalRequest.setGestedBy("Original user");

    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(originalRequest));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), eq(originalRequest), eq(updateDto));
    when(requestRepository.save(any(Request.class))).thenReturn(originalRequest);

    Request result = requestService.updateRequest("USER001", updateDto);

    assertEquals(RequestStatus.REJECTED, result.getStatus());
    assertEquals("Original answer", result.getAnswer());
    assertEquals("Original user", result.getGestedBy());
  }

  @Test
  void fetchAllRequests_ShouldReturnSortedByCreatedAt() {
    LocalDate now = LocalDate.now();
    Request request1 =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build();
    request1.setCreatedAt(now.minusDays(2));
    Request request2 =
        new Request.RequestBuilder().studentId("STU002").type(RequestType.SWAP).build();
    request2.setCreatedAt(now.minusDays(1));
    Request request3 =
        new Request.RequestBuilder().studentId("STU003").type(RequestType.CANCELLATION).build();
    request3.setCreatedAt(now);

    List<Request> unsortedList = List.of(request3, request1, request2);
    when(requestRepository.findAll()).thenReturn(unsortedList);

    List<Request> result = requestService.fetchAllRequests();

    assertEquals(3, result.size());
    assertEquals(now.minusDays(2), result.get(0).getCreatedAt());
    assertEquals(now.minusDays(1), result.get(1).getCreatedAt());
    assertEquals(now, result.get(2).getCreatedAt());
  }

  @Test
  void fetchAllRequests_ShouldHandleEmptyList() {
    when(requestRepository.findAll()).thenReturn(List.of());

    List<Request> result = requestService.fetchAllRequests();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void fetchAllRequests_ShouldThrowBusinessException_WhenRepositoryFails() {
    when(requestRepository.findAll()).thenThrow(new RuntimeException("Database error"));

    assertThrows(BusinessException.class, () -> requestService.fetchAllRequests());
  }

  @Test
  void fetchRequestsByFacultyName_ShouldReturnSortedByCreatedAtDescending() {
    String facultyName = "Engineering";
    LocalDate now = LocalDate.now();

    Request request1 =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build();
    request1.setCreatedAt(now.minusDays(2));
    Request request2 =
        new Request.RequestBuilder().studentId("STU002").type(RequestType.SWAP).build();
    request2.setCreatedAt(now.minusDays(1));
    Request request3 =
        new Request.RequestBuilder().studentId("STU003").type(RequestType.CANCELLATION).build();
    request3.setCreatedAt(now);

    List<Request> allRequests = List.of(request1, request2, request3);

    when(requestRepository.findAll()).thenReturn(allRequests);
    when(studentService.getFacultyByStudentId("STU001")).thenReturn("Engineering");
    when(studentService.getFacultyByStudentId("STU002")).thenReturn("Engineering");
    when(studentService.getFacultyByStudentId("STU003")).thenReturn("Engineering");
    doNothing().when(requestValidator).validateFacultyName(facultyName);

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertEquals(3, result.size());
    assertEquals(now, result.get(0).getCreatedAt());
    assertEquals(now.minusDays(1), result.get(1).getCreatedAt());
    assertEquals(now.minusDays(2), result.get(2).getCreatedAt());
  }

  @Test
  void fetchRequestsByFacultyName_ShouldFilterByFacultyCorrectly() {
    String facultyName = "Engineering";

    Request engRequest1 =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build();
    Request engRequest2 =
        new Request.RequestBuilder().studentId("STU002").type(RequestType.SWAP).build();
    Request medRequest =
        new Request.RequestBuilder().studentId("STU003").type(RequestType.CANCELLATION).build();

    List<Request> allRequests = List.of(engRequest1, engRequest2, medRequest);

    when(requestRepository.findAll()).thenReturn(allRequests);
    when(studentService.getFacultyByStudentId("STU001")).thenReturn("Engineering");
    when(studentService.getFacultyByStudentId("STU002")).thenReturn("Engineering");
    when(studentService.getFacultyByStudentId("STU003")).thenReturn("Medicine");
    doNothing().when(requestValidator).validateFacultyName(facultyName);

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertEquals(2, result.size());
    assertTrue(
        result.stream()
            .allMatch(
                req -> req.getStudentId().equals("STU001") || req.getStudentId().equals("STU002")));
  }

  @Test
  void countByGroupCodesAndStatus_ShouldHandleEmptyGroupCodes() {
    List<String> emptyGroupCodes = List.of();
    RequestStatus status = RequestStatus.PENDING;

    when(requestRepository.countByGroupCodesAndStatus(emptyGroupCodes, status)).thenReturn(0);

    Integer result = requestService.countByGroupCodesAndStatus(emptyGroupCodes, status);

    assertEquals(0, result);
    verify(requestRepository).countByGroupCodesAndStatus(emptyGroupCodes, status);
  }

  @Test
  void countByGroupCodesAndStatus_ShouldHandleAllStatusTypes() {
    List<String> groupCodes = List.of("GROUP001", "GROUP002");

    for (RequestStatus status : RequestStatus.values()) {
      when(requestRepository.countByGroupCodesAndStatus(groupCodes, status))
          .thenReturn(status.ordinal() + 1);

      Integer result = requestService.countByGroupCodesAndStatus(groupCodes, status);

      assertEquals(status.ordinal() + 1, result);
      verify(requestRepository).countByGroupCodesAndStatus(groupCodes, status);

      reset(requestRepository);
    }
  }

  @Test
  void countByGroupCodesAndType_ShouldHandleAllRequestTypes() {
    List<String> groupCodes = List.of("GROUP001");

    for (RequestType type : RequestType.values()) {
      when(requestRepository.countByGroupCodesAndType(groupCodes, type))
          .thenReturn(type.ordinal() + 1);

      Integer result = requestService.countByGroupCodesAndType(groupCodes, type);

      assertEquals(type.ordinal() + 1, result);
      verify(requestRepository).countByGroupCodesAndType(groupCodes, type);

      reset(requestRepository);
    }
  }

  @Test
  void countByStatus_ShouldHandleAllStatusTypes() {
    for (RequestStatus status : RequestStatus.values()) {
      when(requestRepository.countByStatus(status)).thenReturn(status.ordinal() * 10);

      Integer result = requestService.countByStatus(status);

      assertEquals(status.ordinal() * 10, result);
      verify(requestRepository).countByStatus(status);

      reset(requestRepository);
    }
  }

  @Test
  void countByType_ShouldHandleAllRequestTypes() {
    for (RequestType type : RequestType.values()) {
      when(requestRepository.countByType(type)).thenReturn(type.ordinal() * 5);

      Integer result = requestService.countByType(type);

      assertEquals(type.ordinal() * 5, result);
      verify(requestRepository).countByType(type);

      reset(requestRepository);
    }
  }

  @Test
  void createRequest_ShouldHandleAllRequestTypesWithDifferentFieldCombinations() {
    for (RequestType type : RequestType.values()) {
      CreateRequestDto createDto;

      switch (type) {
        case JOIN:
          createDto = new CreateRequestDto("STU001", type, "Join description", null, "GROUP001");
          break;
        case SWAP:
          createDto =
              new CreateRequestDto("STU001", type, "Swap description", "GROUP001", "GROUP002");
          break;
        case CANCELLATION:
          createDto = new CreateRequestDto("STU001", type, "Cancel description", "GROUP001", null);
          break;
        default:
          continue;
      }

      when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
      doNothing().when(requestValidator).validateCreateRequest(createDto);
      when(requestRepository.save(any(Request.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      Request result = requestService.createRequest(createDto);

      assertNotNull(result);
      assertEquals(type, result.getType());
      assertEquals("STU001", result.getStudentId());
      assertEquals(RequestStatus.PENDING, result.getStatus());
      assertNotNull(result.getRequestId());
      assertNotNull(result.getCreatedAt());

      reset(requestPeriodService, requestValidator, requestRepository);
    }
  }

  private void injectMockStrategy(Role role, QueryStrategy mockStrategy) {
    try {
      Field strategyMapField = RequestService.class.getDeclaredField("strategyMap");
      strategyMapField.setAccessible(true);

      @SuppressWarnings("unchecked")
      Map<Role, QueryStrategy> strategyMap =
          (Map<Role, QueryStrategy>) strategyMapField.get(requestService);

      Map<Role, QueryStrategy> newStrategyMap = new HashMap<>(strategyMap);
      newStrategyMap.put(role, mockStrategy);

      strategyMapField.set(requestService, newStrategyMap);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject mock strategy", e);
    }
  }

  private void invokePrivateMethod(Object object, String methodName, Object... args) {
    try {
      Method method = object.getClass().getDeclaredMethod(methodName, getParameterTypes(args));
      method.setAccessible(true);
      method.invoke(object, args);
    } catch (Exception e) {
      throw new RuntimeException("Failed to invoke private method", e);
    }
  }

  private Class<?>[] getParameterTypes(Object... args) {
    return Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
  }

  @Test
  void countByGroupCodesAndStatus_ShouldHandleNullGroupCodes() {
    RequestStatus status = RequestStatus.PENDING;

    when(requestRepository.countByGroupCodesAndStatus(null, status)).thenReturn(0);

    Integer result = requestService.countByGroupCodesAndStatus(null, status);

    assertEquals(0, result);
  }

  @Test
  void countByGroupCodesAndType_ShouldHandleNullInputs() {
    when(requestRepository.countByGroupCodesAndType(null, null)).thenReturn(0);

    Integer result = requestService.countByGroupCodesAndType(null, null);

    assertEquals(0, result);
  }

  @Test
  void countByStatus_ShouldHandleNullStatus() {
    when(requestRepository.countByStatus(null)).thenReturn(0);

    Integer result = requestService.countByStatus(null);

    assertEquals(0, result);
  }

  @Test
  void countByType_ShouldHandleNullType() {
    when(requestRepository.countByType(null)).thenReturn(0);

    Integer result = requestService.countByType(null);

    assertEquals(0, result);
  }

  @Test
  void updateRequest_ShouldHandleActivePeriodException() {
    when(requestPeriodService.getActivePeriod())
        .thenThrow(new ResourceNotFoundException("No active period"));

    assertThrows(
        BusinessException.class, () -> requestService.updateRequest("USER001", updateRequestDto));
  }

  @Test
  void updateRequest_ShouldHandleValidatorExceptions() {
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doThrow(new BusinessException("Validation failed"))
        .when(requestValidator)
        .validateUpdateRequest(anyString(), eq(mockRequest1), eq(updateRequestDto));

    assertThrows(
        BusinessException.class, () -> requestService.updateRequest("USER001", updateRequestDto));
  }

  @Test
  void fetchRequests_ShouldThrowBusinessException_WhenDeanStrategyFails() {
    Role role = Role.DEAN;
    String userId = "DEAN001";

    QueryStrategy mockStrategy = mock(QueryStrategy.class);
    when(mockStrategy.queryRequests(userId)).thenThrow(new RuntimeException("Dean service error"));

    injectMockStrategy(role, mockStrategy);

    assertThrows(RuntimeException.class, () -> requestService.fetchRequests(role, userId));
  }

  @Test
  void fetchRequests_ShouldThrowBusinessException_WhenProfessorStrategyFails() {
    Role role = Role.PROFESSOR;
    String userId = "PROF001";

    QueryStrategy mockStrategy = mock(QueryStrategy.class);
    when(mockStrategy.queryRequests(userId))
        .thenThrow(new RuntimeException("Professor service down"));

    injectMockStrategy(role, mockStrategy);

    assertThrows(RuntimeException.class, () -> requestService.fetchRequests(role, userId));
  }

  @Test
  void fetchRequests_ShouldThrowBusinessException_WhenStrategyThrowsRuntimeException() {
    Role role = Role.STUDENT;
    String userId = "STU001";

    QueryStrategy mockStrategy = mock(QueryStrategy.class);
    when(mockStrategy.queryRequests(userId))
        .thenThrow(new RuntimeException("Strategy execution failed"));

    injectMockStrategy(role, mockStrategy);

    assertThrows(RuntimeException.class, () -> requestService.fetchRequests(role, userId));
  }

  @Test
  void fetchRequests_ShouldThrowBusinessException_WhenStudentStrategyFails() {
    Role role = Role.STUDENT;
    String userId = "STU001";

    QueryStrategy mockStrategy = mock(QueryStrategy.class);
    when(mockStrategy.queryRequests(userId))
        .thenThrow(new RuntimeException("Student service unavailable"));

    injectMockStrategy(role, mockStrategy);

    assertThrows(RuntimeException.class, () -> requestService.fetchRequests(role, userId));
  }

  @Test
  void getRequestStats_ShouldHandleRepositoryExceptions() {
    when(requestRepository.count()).thenThrow(new RuntimeException("Count failed"));

    assertThrows(RuntimeException.class, () -> requestService.getRequestStats());
  }

  @Test
  void fetchRequestsByFacultyName_ShouldHandleRepositoryExceptions() {
    String facultyName = "Engineering";

    when(requestRepository.findAll()).thenThrow(new RuntimeException("Database error"));
    doNothing().when(requestValidator).validateFacultyName(facultyName);

    assertThrows(
        RuntimeException.class, () -> requestService.fetchRequestsByFacultyName(facultyName));
  }

  @Test
  void fetchRequestsByFacultyName_ShouldHandleNullFacultyFromStudentService() {
    String facultyName = "Engineering";

    when(requestRepository.findAll()).thenReturn(List.of(mockRequest1));
    when(studentService.getFacultyByStudentId("STU001")).thenReturn("Engineering");
    doNothing().when(requestValidator).validateFacultyName(facultyName);

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertEquals(1, result.size());
    assertEquals("STU001", result.get(0).getStudentId());
  }

  @Test
  void updateRequest_ShouldHandleAnswerStrategyExceptions() {
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), eq(mockRequest1), eq(updateRequestDto));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    doThrow(new RuntimeException("Strategy failed"))
        .when(answerStrategy)
        .answerRequest(any(Request.class));

    assertThrows(
        BusinessException.class, () -> requestService.updateRequest("USER001", updateRequestDto));
  }

  @Test
  void updateRequest_ShouldThrowBusinessException_WhenAnswerStrategyFails() {
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(requestValidator)
        .validateUpdateRequest(anyString(), eq(mockRequest1), eq(updateRequestDto));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    doThrow(new RuntimeException("Strategy execution failed"))
        .when(answerStrategy)
        .answerRequest(any(Request.class));

    assertThrows(
        BusinessException.class, () -> requestService.updateRequest("USER001", updateRequestDto));
  }

  @Test
  void countByGroupCodesAndStatus_ShouldHandleRepositoryExceptions() {
    List<String> groupCodes = List.of("GROUP001");
    RequestStatus status = RequestStatus.PENDING;

    when(requestRepository.countByGroupCodesAndStatus(groupCodes, status))
        .thenThrow(new RuntimeException("Query failed"));

    assertThrows(
        BusinessException.class,
        () -> requestService.countByGroupCodesAndStatus(groupCodes, status));
  }

  @Test
  void countByGroupCodesAndType_ShouldHandleRepositoryExceptions() {
    List<String> groupCodes = List.of("GROUP001");
    RequestType type = RequestType.JOIN;

    when(requestRepository.countByGroupCodesAndType(groupCodes, type))
        .thenThrow(new RuntimeException("Query failed"));

    assertThrows(
        BusinessException.class, () -> requestService.countByGroupCodesAndType(groupCodes, type));
  }

  @Test
  void createRequest_ShouldThrowBusinessException_WhenSaveFailsWithDataIntegrity() {
    when(requestPeriodService.getActivePeriod()).thenReturn(activePeriod);
    doNothing().when(requestValidator).validateCreateRequest(createRequestDto);
    when(requestRepository.save(any(Request.class)))
        .thenThrow(new RuntimeException("Duplicate key violation"));

    assertThrows(BusinessException.class, () -> requestService.createRequest(createRequestDto));
  }

  @Test
  void deleteRequestStatus_ShouldThrowBusinessException_WhenDeleteFailsWithConstraint() {
    String requestId = "REQ001";
    Request request =
        new Request.RequestBuilder().studentId("STU001").type(RequestType.JOIN).build();

    when(requestRepository.findByRequestId(requestId)).thenReturn(Optional.of(request));
    doThrow(new RuntimeException("Foreign key constraint violation"))
        .when(requestRepository)
        .delete(request);

    assertThrows(BusinessException.class, () -> requestService.deleteRequestStatus(requestId));
  }

  @Test
  void countByStatus_ShouldHandleRepositoryExceptions() {
    RequestStatus status = RequestStatus.PENDING;

    when(requestRepository.countByStatus(status)).thenThrow(new RuntimeException("Count failed"));

    assertThrows(BusinessException.class, () -> requestService.countByStatus(status));
  }

  @Test
  void countByType_ShouldHandleRepositoryExceptions() {
    RequestType type = RequestType.JOIN;

    when(requestRepository.countByType(type)).thenThrow(new RuntimeException("Count failed"));

    assertThrows(BusinessException.class, () -> requestService.countByType(type));
  }

  @Test
  void countTotalRequests_ShouldHandleRepositoryExceptions() {
    when(requestRepository.count()).thenThrow(new RuntimeException("Count failed"));

    assertThrows(BusinessException.class, () -> requestService.countTotalRequests());
  }

  @Test
  void countByGroupCodes_ShouldHandleRepositoryExceptions() {
    List<String> groupCodes = List.of("GROUP001");

    when(requestRepository.countByGroupCodes(groupCodes))
        .thenThrow(new RuntimeException("Query failed"));

    assertThrows(BusinessException.class, () -> requestService.countByGroupCodes(groupCodes));
  }

  @Test
  void fetchAllRequests_ShouldHandleRepositoryExceptions() {
    when(requestRepository.findAll()).thenThrow(new RuntimeException("Database error"));

    assertThrows(BusinessException.class, () -> requestService.fetchAllRequests());
  }

  @Test
  void getRequestStats_ShouldHandlePartialRepositoryFailures() {
    when(requestRepository.count()).thenReturn(100L);
    when(requestRepository.countByStatus(RequestStatus.PENDING)).thenReturn(30);
    when(requestRepository.countByStatus(RequestStatus.ACCEPTED))
        .thenThrow(new RuntimeException("DB error"));

    assertThrows(RuntimeException.class, () -> requestService.getRequestStats());
  }

  @Test
  void getRequestStats_ShouldHandleRepositoryExceptionsInCount() {
    when(requestRepository.count()).thenThrow(new RuntimeException("Count failed"));

    assertThrows(RuntimeException.class, () -> requestService.getRequestStats());
  }

  @Test
  void getRequestStatsByUserId_WithStudentRole_ShouldReturnCorrectPercentages() {
    String userId = "student-123";
    Role role = Role.STUDENT;

    when(requestRepository.countByStudentId(userId)).thenReturn(10);
    when(requestRepository.countByStudentIdAndStatus(userId, RequestStatus.PENDING)).thenReturn(3);
    when(requestRepository.countByStudentIdAndStatus(userId, RequestStatus.ACCEPTED)).thenReturn(4);
    when(requestRepository.countByStudentIdAndStatus(userId, RequestStatus.REJECTED)).thenReturn(2);
    when(requestRepository.countByStudentIdAndStatus(userId, RequestStatus.WAITING)).thenReturn(1);
    when(requestRepository.countByStudentIdAndStatus(userId, RequestStatus.IN_REVIEW))
        .thenReturn(0);
    when(requestRepository.countByStudentIdAndType(userId, RequestType.JOIN)).thenReturn(5);
    when(requestRepository.countByStudentIdAndType(userId, RequestType.SWAP)).thenReturn(3);
    when(requestRepository.countByStudentIdAndType(userId, RequestType.CANCELLATION)).thenReturn(2);

    List<Double> result = requestService.getRequestStatsByUserId(userId, role);

    assertEquals(8, result.size());
    assertEquals(30.0, result.get(0));
    assertEquals(40.0, result.get(1));
    assertEquals(20.0, result.get(2));
    assertEquals(10.0, result.get(3));
    assertEquals(0.0, result.get(4));
    assertEquals(50.0, result.get(5));
    assertEquals(30.0, result.get(6));
    assertEquals(20.0, result.get(7));
  }

  @Test
  void getRequestStatsByUserId_WithStudentRoleAndZeroTotal_ShouldReturnZeros() {
    String userId = "student-123";
    Role role = Role.STUDENT;

    when(requestRepository.countByStudentId(userId)).thenReturn(0);

    List<Double> result = requestService.getRequestStatsByUserId(userId, role);

    assertEquals(4, result.size());
    assertEquals(Arrays.asList(0.0, 0.0, 0.0, 0.0), result);
  }

  @Test
  void getRequestStatsByUserId_WithProfessorRole_ShouldReturnCorrectPercentages() {
    String userId = "prof-123";
    Role role = Role.PROFESSOR;

    when(requestRepository.countByGestedBy(userId)).thenReturn(20);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.PENDING))
        .thenReturn(5);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.ACCEPTED))
        .thenReturn(10);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.REJECTED))
        .thenReturn(3);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.WAITING))
        .thenReturn(1);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.IN_REVIEW))
        .thenReturn(1);
    when(requestRepository.countByGestedByAndType(userId, RequestType.JOIN)).thenReturn(12);
    when(requestRepository.countByGestedByAndType(userId, RequestType.SWAP)).thenReturn(6);
    when(requestRepository.countByGestedByAndType(userId, RequestType.CANCELLATION)).thenReturn(2);

    List<Double> result = requestService.getRequestStatsByUserId(userId, role);

    assertEquals(8, result.size());
    assertEquals(25.0, result.get(0));
    assertEquals(50.0, result.get(1));
    assertEquals(15.0, result.get(2));
    assertEquals(5.0, result.get(3));
    assertEquals(5.0, result.get(4));
    assertEquals(60.0, result.get(5));
    assertEquals(30.0, result.get(6));
    assertEquals(10.0, result.get(7));
  }

  @Test
  void getRequestStatsByUserId_WithDeanRole_ShouldReturnCorrectPercentages() {
    String userId = "dean-123";
    Role role = Role.DEAN;

    when(requestRepository.countByGestedBy(userId)).thenReturn(15);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.PENDING))
        .thenReturn(3);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.ACCEPTED))
        .thenReturn(8);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.REJECTED))
        .thenReturn(4);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.WAITING))
        .thenReturn(0);
    when(requestRepository.countByGestedByAndRequestStatus(userId, RequestStatus.IN_REVIEW))
        .thenReturn(0);
    when(requestRepository.countByGestedByAndType(userId, RequestType.JOIN)).thenReturn(10);
    when(requestRepository.countByGestedByAndType(userId, RequestType.SWAP)).thenReturn(4);
    when(requestRepository.countByGestedByAndType(userId, RequestType.CANCELLATION)).thenReturn(1);

    List<Double> result = requestService.getRequestStatsByUserId(userId, role);

    assertEquals(8, result.size());
    assertEquals(20.0, result.get(0));
    assertEquals(53.33, result.get(1), 0.01);
    assertEquals(26.67, result.get(2), 0.01);
    assertEquals(0.0, result.get(3));
    assertEquals(0.0, result.get(4));
    assertEquals(66.67, result.get(5), 0.01);
    assertEquals(26.67, result.get(6), 0.01);
    assertEquals(6.67, result.get(7), 0.01);
  }

  @Test
  void getRequestStatsByUserId_WithAdministratorRole_ShouldThrowBusinessException() {
    String userId = "admin-123";
    Role role = Role.ADMINISTRATOR;

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> requestService.getRequestStatsByUserId(userId, role));

    assertEquals("Administrator does no have specific stats for requests", exception.getMessage());
  }

  @Test
  void getRequestStatsByUserId_WithRepositoryException_ShouldThrowBusinessException() {
    String userId = "student-123";
    Role role = Role.STUDENT;

    when(requestRepository.countByStudentId(userId))
        .thenThrow(new RuntimeException("Database error"));

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> requestService.getRequestStatsByUserId(userId, role));

    assertTrue(exception.getMessage().contains("Failed to get request stats by student"));
  }

  @Test
  void getRequestStats_ShouldReturnCorrectStats() {
    when(requestRepository.count()).thenReturn(100L);
    when(requestRepository.countByStatus(RequestStatus.PENDING)).thenReturn(30);
    when(requestRepository.countByStatus(RequestStatus.ACCEPTED)).thenReturn(50);
    when(requestRepository.countByStatus(RequestStatus.REJECTED)).thenReturn(20);

    RequestStats result = requestService.getRequestStats();

    assertNotNull(result);
    assertEquals(100, result.total());
    assertEquals(30, result.pending());
    assertEquals(50, result.approved());
    assertEquals(20, result.rejected());
  }

  @Test
  void getRequestStats_WithZeroRequests_ShouldReturnZeroStats() {
    when(requestRepository.count()).thenReturn(0L);
    when(requestRepository.countByStatus(RequestStatus.PENDING)).thenReturn(0);
    when(requestRepository.countByStatus(RequestStatus.ACCEPTED)).thenReturn(0);
    when(requestRepository.countByStatus(RequestStatus.REJECTED)).thenReturn(0);

    RequestStats result = requestService.getRequestStats();

    assertNotNull(result);
    assertEquals(0, result.total());
    assertEquals(0, result.pending());
    assertEquals(0, result.approved());
    assertEquals(0, result.rejected());
  }

  @Test
  void fetchRequestsByFacultyName_WithValidFaculty_ShouldReturnSortedRequests() {
    String facultyName = "Engineering";

    Request request1 = new Request();
    request1.setRequestId("req1");
    request1.setStudentId("student1");
    request1.setCreatedAt(LocalDate.of(2024, 1, 10));

    Request request2 = new Request();
    request2.setRequestId("req2");
    request2.setStudentId("student2");
    request2.setCreatedAt(LocalDate.of(2024, 1, 15));

    Request request3 = new Request();
    request3.setRequestId("req3");
    request3.setStudentId("student3");
    request3.setCreatedAt(LocalDate.of(2024, 1, 5));

    List<Request> allRequests = Arrays.asList(request1, request2, request3);

    when(requestRepository.findAll()).thenReturn(allRequests);
    when(studentService.getFacultyByStudentId("student1")).thenReturn("Engineering");
    when(studentService.getFacultyByStudentId("student2")).thenReturn("Engineering");
    when(studentService.getFacultyByStudentId("student3")).thenReturn("Science");

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertEquals(2, result.size());
    assertEquals("req2", result.get(0).getRequestId());
    assertEquals("req1", result.get(1).getRequestId());
  }

  @Test
  void fetchRequestsByFacultyName_WithNoMatchingRequests_ShouldReturnEmptyList() {
    String facultyName = "Engineering";

    Request request1 = new Request();
    request1.setRequestId("req1");
    request1.setStudentId("student1");
    request1.setCreatedAt(LocalDate.of(2024, 1, 10));

    List<Request> allRequests = List.of(request1);

    when(requestRepository.findAll()).thenReturn(allRequests);
    when(studentService.getFacultyByStudentId("student1")).thenReturn("Science");

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertTrue(result.isEmpty());
  }

  @Test
  void fetchRequestsByFacultyName_WithInvalidFaculty_ShouldThrowBusinessException() {
    String facultyName = "InvalidFaculty";

    doThrow(new BusinessException("Faculty InvalidFaculty does not exist"))
        .when(requestValidator)
        .validateFacultyName(facultyName);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> requestService.fetchRequestsByFacultyName(facultyName));

    assertTrue(exception.getMessage().contains("does not exist"));
  }
}
