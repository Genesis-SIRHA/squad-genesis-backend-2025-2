package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreateRequestDto;
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
import edu.dosw.services.strategy.AnswerStrategies.AnswerStrategy;
import edu.dosw.services.strategy.AnswerStrategies.AnswerStrategyFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

  @Mock private RequestRepository requestRepository;

  @Mock private ValidatorService validatorService;

  @Mock private AuthenticationService authenticationService;

  @Mock private StudentService studentService;

  @Mock private DeanService deanService;

  @Mock private ProfessorService professorService;

  @Mock private AnswerStrategyFactory answerStrategyFactory;

  @Mock private AnswerStrategy answerStrategy;

  @InjectMocks private RequestService requestService;

  private Request mockRequest1;
  private Request mockRequest2;
  private CreateRequestDto createRequestDto;
  private UpdateRequestDto updateRequestDto;

  @BeforeEach
  void setUp() {
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
    mockRequest1.setCreatedAt(LocalDate.of(2024, 1, 15));

    mockRequest2 =
        new Request.RequestBuilder()
            .studentId("STU002")
            .type(RequestType.CANCELLATION)
            .description("Request to cancel enrollment")
            .originGroupId("GROUP003")
            .build();
    mockRequest2.setRequestId("REQ002");
    mockRequest2.setStatus(RequestStatus.ACCEPTED);
    mockRequest2.setCreatedAt(LocalDate.of(2024, 1, 10));

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
    doNothing().when(validatorService).validateCreateRequest(createRequestDto);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    Request result = requestService.createRequest(createRequestDto);

    assertNotNull(result);
    verify(validatorService).validateCreateRequest(createRequestDto);
    verify(requestRepository).save(any(Request.class));
  }

  @Test
  void createRequest_ShouldThrowBusinessException_WhenRepositoryFails() {
    doNothing().when(validatorService).validateCreateRequest(createRequestDto);
    when(requestRepository.save(any(Request.class))).thenThrow(new RuntimeException("Save failed"));

    assertThrows(BusinessException.class, () -> requestService.createRequest(createRequestDto));
  }

  @Test
  void updateRequest_ShouldUpdateAndReturnRequest_WhenValid() {
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(validatorService)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    doNothing().when(answerStrategy).answerRequest(any(Request.class));
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    Request result = requestService.updateRequest("USER001", updateRequestDto);

    assertNotNull(result);
    assertEquals(RequestStatus.ACCEPTED, result.getStatus());
    verify(requestRepository).findByRequestId("REQ001");
    verify(answerStrategy).answerRequest(any(Request.class));
    verify(requestRepository).save(any(Request.class));
  }

  @Test
  void updateRequest_ShouldNotCallAnswerStrategy_WhenStatusNotAccepted() {
    UpdateRequestDto rejectedDto =
        new UpdateRequestDto("REQ001", RequestStatus.REJECTED, "Request rejected", "DEAN001");

    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(validatorService)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    Request result = requestService.updateRequest("USER001", rejectedDto);

    assertNotNull(result);
    verify(answerStrategyFactory, never()).getStrategy(any(RequestType.class));
    verify(requestRepository).save(any(Request.class));
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
    doNothing().when(validatorService).validateFacultyName(facultyName);

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("STU001", result.get(0).getStudentId());
    verify(validatorService).validateFacultyName(facultyName);
    verify(requestRepository).findAll();
  }

  @Test
  void fetchRequestsByFacultyName_ShouldReturnEmptyList_WhenNoMatches() {
    String facultyName = "Engineering";

    when(requestRepository.findAll()).thenReturn(Arrays.asList(mockRequest1, mockRequest2));
    when(studentService.getFacultyByStudentId(anyString())).thenReturn("Medicine");
    doNothing().when(validatorService).validateFacultyName(facultyName);

    List<Request> result = requestService.fetchRequestsByFacultyName(facultyName);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void updateRequest_ShouldUpdateManagedBy_WhenProvided() {
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(validatorService)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    requestService.updateRequest("USER001", updateRequestDto);

    verify(requestRepository).save(argThat(request -> request.getGestedBy().equals("DEAN001")));
  }

  @Test
  void updateRequest_ShouldUpdateAnswer_WhenProvided() {
    when(requestRepository.findByRequestId("REQ001")).thenReturn(Optional.of(mockRequest1));
    doNothing()
        .when(validatorService)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.SWAP)).thenReturn(answerStrategy);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    requestService.updateRequest("USER001", updateRequestDto);

    verify(requestRepository)
        .save(argThat(request -> request.getAnswer().equals("Request approved")));
  }

  @Test
  void createRequest_ShouldBuildRequestWithCorrectFields() {
    doNothing().when(validatorService).validateCreateRequest(createRequestDto);
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

    when(requestRepository.findByRequestId("REQ003")).thenReturn(Optional.of(joinRequest));
    doNothing()
        .when(validatorService)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.JOIN)).thenReturn(answerStrategy);
    doNothing().when(answerStrategy).answerRequest(any(Request.class));
    when(requestRepository.save(any(Request.class))).thenReturn(joinRequest);

    Request result = requestService.updateRequest("USER001", joinUpdateDto);

    assertNotNull(result);
    verify(answerStrategyFactory).getStrategy(RequestType.JOIN);
    verify(answerStrategy).answerRequest(any(Request.class));
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

    when(requestRepository.findByRequestId("REQ004")).thenReturn(Optional.of(cancellationRequest));
    doNothing()
        .when(validatorService)
        .validateUpdateRequest(anyString(), any(Request.class), any(UpdateRequestDto.class));
    when(answerStrategyFactory.getStrategy(RequestType.CANCELLATION)).thenReturn(answerStrategy);
    doNothing().when(answerStrategy).answerRequest(any(Request.class));
    when(requestRepository.save(any(Request.class))).thenReturn(cancellationRequest);

    Request result = requestService.updateRequest("USER001", cancellationUpdateDto);

    assertNotNull(result);
    verify(answerStrategyFactory).getStrategy(RequestType.CANCELLATION);
    verify(answerStrategy).answerRequest(any(Request.class));
  }

  @Test
  void createRequest_ShouldHandleJoinRequestType() {
    CreateRequestDto joinRequestDto =
        new CreateRequestDto("STU005", RequestType.JOIN, "Request to join group", "GROUP003", null);

    doNothing().when(validatorService).validateCreateRequest(joinRequestDto);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    Request result = requestService.createRequest(joinRequestDto);

    assertNotNull(result);
    verify(validatorService).validateCreateRequest(joinRequestDto);
    verify(requestRepository).save(any(Request.class));
  }

  @Test
  void createRequest_ShouldHandleCancellationRequestType() {
    CreateRequestDto cancellationRequestDto =
        new CreateRequestDto(
            "STU006", RequestType.CANCELLATION, "Request to cancel enrollment", null, "GROUP004");

    doNothing().when(validatorService).validateCreateRequest(cancellationRequestDto);
    when(requestRepository.save(any(Request.class))).thenReturn(mockRequest1);

    Request result = requestService.createRequest(cancellationRequestDto);

    assertNotNull(result);
    verify(validatorService).validateCreateRequest(cancellationRequestDto);
    verify(requestRepository).save(any(Request.class));
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
}
