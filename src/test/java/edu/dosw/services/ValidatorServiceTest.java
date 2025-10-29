package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CreateRequestDto;
import edu.dosw.dto.UpdateRequestDto;
import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import edu.dosw.model.Student;
import edu.dosw.model.enums.RequestStatus;
import edu.dosw.model.enums.RequestType;
import edu.dosw.services.UserServices.StudentService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {

  @Mock private GroupService groupService;

  @Mock private StudentService studentService;

  @Mock private AuthenticationService authenticationService;

  @Mock private FacultyService facultyService;

  @InjectMocks private ValidatorService validatorService;

  private Student testStudent;
  private Group testGroup;
  private Faculty testFaculty;
  private Course testCourse;
  private CreateRequestDto joinRequest;
  private CreateRequestDto swapRequest;
  private CreateRequestDto cancellationRequest;

  @BeforeEach
  void setUp() {
    testStudent = new Student();
    testStudent.setUserId("STU001");
    testStudent.setFacultyName("Engineering");
    testStudent.setPlan("2024");

    testGroup = new Group();
    testGroup.setGroupCode("GROUP001");
    testGroup.setAbbreviation("MATH101");

    testCourse = new Course();
    testCourse.setAbbreviation("MATH101");

    testFaculty = new Faculty("Engineering", "2024", List.of(testCourse));

    joinRequest =
        new CreateRequestDto("STU001", RequestType.JOIN, "Join request", null, "GROUP001");

    swapRequest =
        new CreateRequestDto("STU001", RequestType.SWAP, "Swap request", "GROUP001", "GROUP002");

    cancellationRequest =
        new CreateRequestDto(
            "STU001", RequestType.CANCELLATION, "Cancellation request", "GROUP001", null);
  }

  @Test
  void validateCreateRequest_SwapRequest_WithoutOriginGroup_ShouldThrowException() {
    CreateRequestDto invalidSwapRequest =
        new CreateRequestDto("STU001", RequestType.SWAP, "Swap request", null, "GROUP002");

    assertThrows(
        BusinessException.class, () -> validatorService.validateCreateRequest(invalidSwapRequest));
  }

  @Test
  void validateCreateRequest_JoinRequest_WithValidData_ShouldNotThrowException() {
    when(studentService.getStudentById("STU001")).thenReturn(testStudent);
    when(groupService.getGroupByGroupCode("GROUP001")).thenReturn(testGroup);
    when(facultyService.getFacultyByNameAndPlan("Engineering", "2024")).thenReturn(testFaculty);

    assertDoesNotThrow(() -> validatorService.validateCreateRequest(joinRequest));
  }

  @Test
  void validateCreateRequest_CancellationRequest_WithValidData_ShouldNotThrowException() {
    when(studentService.getStudentById("STU001")).thenReturn(testStudent);
    when(groupService.getGroupByGroupCode("GROUP001")).thenReturn(testGroup);

    assertDoesNotThrow(() -> validatorService.validateCreateRequest(cancellationRequest));
  }

  @Test
  void validateCreateRequest_SwapRequest_WithValidData_ShouldNotThrowException() {
    when(studentService.getStudentById("STU001")).thenReturn(testStudent);
    when(groupService.getGroupByGroupCode("GROUP001")).thenReturn(testGroup);
    when(groupService.getGroupByGroupCode("GROUP002")).thenReturn(testGroup);
    when(facultyService.getFacultyByNameAndPlan("Engineering", "2024")).thenReturn(testFaculty);

    assertDoesNotThrow(() -> validatorService.validateCreateRequest(swapRequest));
  }

  @Test
  void validateUpdateRequest_WithNullUser_ShouldThrowResourceNotFoundException() {
    String userId = "USER001";
    Request request = new Request();
    request.setStatus(RequestStatus.PENDING);
    UpdateRequestDto updateRequestDto =
        new UpdateRequestDto("REQ001", RequestStatus.ACCEPTED, "Approved", "ADMIN001");

    when(authenticationService.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> validatorService.validateUpdateRequest(userId, request, updateRequestDto));
  }

  @Test
  void validateUpdateRequest_WithNullRequest_ShouldThrowResourceNotFoundException() {
    String userId = "USER001";
    UserCredentialsDto user = new UserCredentialsDto("1", "USER001", "user@test.com", "password");
    UpdateRequestDto updateRequestDto =
        new UpdateRequestDto("REQ001", RequestStatus.ACCEPTED, "Approved", "ADMIN001");

    when(authenticationService.findByUserId(userId)).thenReturn(Optional.of(user));

    assertThrows(
        ResourceNotFoundException.class,
        () -> validatorService.validateUpdateRequest(userId, null, updateRequestDto));
  }

  @Test
  void validateUpdateRequest_WithNullStatus_ShouldThrowBusinessException() {
    String userId = "USER001";
    UserCredentialsDto user = new UserCredentialsDto("1", "USER001", "user@test.com", "password");
    Request request = new Request();
    request.setStatus(RequestStatus.PENDING);
    UpdateRequestDto updateRequestDto =
        new UpdateRequestDto("REQ001", null, "Approved", "ADMIN001");

    when(authenticationService.findByUserId(userId)).thenReturn(Optional.of(user));

    assertThrows(
        BusinessException.class,
        () -> validatorService.validateUpdateRequest(userId, request, updateRequestDto));
  }

  @Test
  void validateUpdateRequest_WithSameStatus_ShouldThrowBusinessException() {
    String userId = "USER001";
    UserCredentialsDto user = new UserCredentialsDto("1", "USER001", "user@test.com", "password");
    Request request = new Request();
    request.setStatus(RequestStatus.ACCEPTED);
    UpdateRequestDto updateRequestDto =
        new UpdateRequestDto("REQ001", RequestStatus.ACCEPTED, "Approved", "ADMIN001");

    when(authenticationService.findByUserId(userId)).thenReturn(Optional.of(user));

    assertThrows(
        BusinessException.class,
        () -> validatorService.validateUpdateRequest(userId, request, updateRequestDto));
  }

  @Test
  void validateUpdateRequest_WithNonPendingToPending_ShouldThrowBusinessException() {
    String userId = "USER001";
    UserCredentialsDto user = new UserCredentialsDto("1", "USER001", "user@test.com", "password");
    Request request = new Request();
    request.setStatus(RequestStatus.ACCEPTED);
    UpdateRequestDto updateRequestDto =
        new UpdateRequestDto("REQ001", RequestStatus.PENDING, "Revert", "ADMIN001");

    when(authenticationService.findByUserId(userId)).thenReturn(Optional.of(user));

    assertThrows(
        BusinessException.class,
        () -> validatorService.validateUpdateRequest(userId, request, updateRequestDto));
  }

  @Test
  void validateUpdateRequest_WithValidData_ShouldNotThrowException() {
    String userId = "USER001";
    UserCredentialsDto user = new UserCredentialsDto("1", "USER001", "user@test.com", "password");
    Request request = new Request();
    request.setStatus(RequestStatus.PENDING);
    UpdateRequestDto updateRequestDto =
        new UpdateRequestDto("REQ001", RequestStatus.ACCEPTED, "Approved", "ADMIN001");

    when(authenticationService.findByUserId(userId)).thenReturn(Optional.of(user));

    assertDoesNotThrow(
        () -> validatorService.validateUpdateRequest(userId, request, updateRequestDto));
  }

  @Test
  void validateFacultyName_WithValidFaculty_ShouldNotThrowException() {
    Map<String, String> faculties = Map.of("Engineering", "ENG", "Science", "SCI");
    when(facultyService.getAllFacultyNames()).thenReturn(faculties);

    assertDoesNotThrow(() -> validatorService.validateFacultyName("Engineering"));
  }

  @Test
  void validateFacultyName_WithInvalidFaculty_ShouldThrowBusinessException() {
    Map<String, String> faculties = Map.of("Engineering", "ENG", "Science", "SCI");
    when(facultyService.getAllFacultyNames()).thenReturn(faculties);

    assertThrows(
        BusinessException.class, () -> validatorService.validateFacultyName("InvalidFaculty"));
  }
}
