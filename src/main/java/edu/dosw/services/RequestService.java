package edu.dosw.services;

import edu.dosw.dto.CreateRequestDto;
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
import edu.dosw.services.strategy.queryStrategies.DeanStrategy;
import edu.dosw.services.strategy.queryStrategies.ProfessorStrategy;
import edu.dosw.services.strategy.queryStrategies.QueryStrategy;
import edu.dosw.services.strategy.queryStrategies.StudentStrategy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

  private static final Logger logger = LoggerFactory.getLogger(RequestService.class);
  private final RequestRepository requestRepository;
  private final RequestValidator requestValidator;
  private final StudentService studentService;
  private final Map<Role, QueryStrategy> strategyMap;
  private final AnswerStrategyFactory answerStrategyFactory;
  private final RequestPeriodService requestPeriodService;

  @Autowired
  public RequestService(
      RequestRepository requestRepository,
      RequestValidator requestValidator,
      DeanService deanService,
      ProfessorService professorService,
      StudentService studentService,
      AnswerStrategyFactory answerStrategyFactory,
      RequestPeriodService requestPeriodService) {
    this.requestRepository = requestRepository;
    this.requestValidator = requestValidator;
    this.studentService = studentService;
    this.answerStrategyFactory = answerStrategyFactory;
    this.requestPeriodService = requestPeriodService;
    this.strategyMap =
        Map.of(
            Role.STUDENT, new StudentStrategy(requestRepository, studentService),
            Role.DEAN, new DeanStrategy(requestRepository, deanService),
            Role.PROFESSOR, new ProfessorStrategy(requestRepository, professorService, studentService));
  }

  public List<Request> fetchRequests(Role role, String userId) {
    logger.info("Fetching requests for user: {} with role: {}", userId, role);

    QueryStrategy strategy = strategyMap.get(role);

    if (strategy == null) {
      logger.error("Unsupported role: {}", role);
      throw new BusinessException("Unsupported role: " + role);
    }
    return strategy.queryRequests(userId).stream()
        .sorted(Comparator.comparing(Request::getCreatedAt))
        .toList();
  }

  public List<Request> fetchAllRequests() {
    try {
      return requestRepository.findAll().stream()
          .sorted(Comparator.comparing(Request::getCreatedAt))
          .toList();
    } catch (Exception e) {
      logger.error("Failed to fetch all requests: {}", e.getMessage());
      throw new BusinessException("Failed to fetch all requests: " + e.getMessage());
    }
  }

  public Request createRequest(CreateRequestDto requestDTO) {
    try {
      requestPeriodService.getActivePeriod();
    } catch (ResourceNotFoundException e) {
      logger.error("Request creation period is over");
      throw new BusinessException("Request creation period is over");
    }
    requestValidator.validateCreateRequest(requestDTO);
    Request request =
        new Request.RequestBuilder()
            .studentId(requestDTO.studentId())
            .type(requestDTO.type())
            .description(requestDTO.description())
            .destinationGroupId(requestDTO.destinationGroupId())
            .originGroupId(requestDTO.originGroupId())
            .build();
    try {
      return requestRepository.save(request);
    } catch (Exception e) {
      logger.error("Failed to create request: {}", e.getMessage());
      throw new BusinessException("Failed to create request: " + e.getMessage());
    }
  }

  public Request updateRequest(String userId, UpdateRequestDto updateRequestDto) {
    try {
      requestPeriodService.getActivePeriod();
    } catch (ResourceNotFoundException e) {
      logger.error("Request creation period is over");
      throw new BusinessException("Request creation period is over");
    }
    Request request = requestRepository.findByRequestId(updateRequestDto.requestId()).orElse(null);
    requestValidator.validateUpdateRequest(userId, request, updateRequestDto);

    request.setStatus(updateRequestDto.status());
    if (updateRequestDto.answer() != null) request.setAnswer(updateRequestDto.answer());
    if (updateRequestDto.managedBy() != null) request.setGestedBy(updateRequestDto.managedBy());
    request.setUpdatedAt(LocalDate.now());

    try {
      requestAnswerReplicator(request);
      return requestRepository.save(request);
    } catch (Exception e) {
      logger.error("Failed to update request status: {}", e.getMessage());
      throw new BusinessException("Failed to update request status: " + e.getMessage());
    }
  }

  private void requestAnswerReplicator(Request request) {
    if (request.getStatus() == RequestStatus.ACCEPTED) {
      AnswerStrategy answerStrategy = answerStrategyFactory.getStrategy(request.getType());
      answerStrategy.answerRequest(request);
    }
    if (request.getStatus() == RequestStatus.WAITING) {
      // TODO: Crear la logica para que se mande la notification al estudiante de que debe dar mas
      // informacion sobre una solicitud
      // ideal si se maneja un servicio de notification y que se modifique el get, para que cuando
      // el estado sea waiting front espere una respuesta
    }

    // Para los otros estados no tienen que poner nada, si se cancela o se rechaza no se hace
    // cambios en el resto del sistema :3
    // pd: Sofi, borren esto cuando lo implementen gracias.

  }

  public RequestStats getRequestStats() {
    Integer total = (int) requestRepository.count();
    Integer pending = requestRepository.countByStatus(RequestStatus.PENDING);
    Integer approved = requestRepository.countByStatus(RequestStatus.ACCEPTED);
    Integer rejected = requestRepository.countByStatus(RequestStatus.REJECTED);
    return new RequestStats(total, pending, approved, rejected);
  }

  public Request deleteRequestStatus(String requestId) {
    Request request = requestRepository.findByRequestId(requestId).orElse(null);
    if (request == null) {
      throw new ResourceNotFoundException("Request not found with id: " + requestId);
    }

    try {
      requestRepository.delete(request);
      return request;
    } catch (Exception e) {
      logger.error("Failed to delete request: {}", e.getMessage());
      throw new BusinessException("Failed to delete request: " + e.getMessage());
    }
  }

  public Request getRequest(String requestId) {
    Request request = requestRepository.findByRequestId(requestId).orElse(null);
    if (request == null) {
      logger.error("Request not found with id: {}", requestId);
      throw new ResourceNotFoundException("Request not found with id: " + requestId);
    }
    return request;
  }

  public List<Request> fetchRequestsByFacultyName(String facultyName) {
    requestValidator.validateFacultyName(facultyName);
    List<Request> requests = requestRepository.findAll();
    List<Request> facultyRequest = new ArrayList<>();

    for (Request request : requests) {
      String requestFaculty = studentService.getFacultyByStudentId(request.getStudentId());
      if (requestFaculty.equals(facultyName)) {
        facultyRequest.add(request);
      }
    }
    try {
      return facultyRequest.stream()
          .sorted(Comparator.comparing(Request::getCreatedAt).reversed())
          .toList();
    } catch (Exception e) {
      logger.error("Failed to fetch requests by faculty name: {}", e.getMessage());
      throw new BusinessException("Failed to fetch requests by faculty name: " + e.getMessage());
    }
  }

  public Integer countByGroupCodes(List<String> groupCodes) {
    try {
      return requestRepository.countByGroupCodes(groupCodes);
    } catch (Exception e) {
      logger.error("Failed to count requests by group codes: {}", e.getMessage());
      throw new BusinessException("Failed to count requests by group codes: " + e.getMessage());
    }
  }

  public List<String> getWaitingListOfGroup(String groupCode) {
    List<Request> waitingListRequests = getRequestsByDestinationGroup(groupCode);

    return waitingListRequests.stream()
        .filter(request -> RequestStatus.PENDING.equals(request.getStatus()))
        .map(Request::getStudentId)
        .collect(Collectors.toList());
  }

  private List<Request> getRequestsByDestinationGroup(String destinationGroupCode) {
    List<Request> requests = requestRepository.getRequestByDestinationGroupId(destinationGroupCode);
    if (requests == null) {
      logger.error("Request not found with destination group id: {}", destinationGroupCode);
      throw new RuntimeException(
          "Request not found with destination group id : " + destinationGroupCode);
    }
    return requests;
  }

  public Integer countByGroupCodesAndStatus(List<String> groupCodes, RequestStatus status) {
    try {
      return requestRepository.countByGroupCodesAndStatus(groupCodes, status);
    } catch (Exception e) {
      logger.error("Failed to count requests by group codes and status: {}", e.getMessage());
      throw new BusinessException(
          "Failed to count requests by group codes and status: " + e.getMessage());
    }
  }

  public Integer countByGroupCodesAndType(List<String> groupCodes, RequestType type) {
    try {
      return requestRepository.countByGroupCodesAndType(groupCodes, type);
    } catch (Exception e) {
      logger.error("Failed to count requests by group codes and type: {}", e.getMessage());
      throw new BusinessException(
          "Failed to count requests by group codes and type: " + e.getMessage());
    }
  }

  public Integer countByStatus(RequestStatus status) {
    try {
      return requestRepository.countByStatus(status);
    } catch (Exception e) {
      logger.error("Failed to count requests by status: {}", e.getMessage());
      throw new BusinessException("Failed to count requests by status: " + e.getMessage());
    }
  }

  public Integer countByType(RequestType type) {
    try {
      return requestRepository.countByType(type);
    } catch (Exception e) {
      logger.error("Failed to count requests by type: {}", e.getMessage());
      throw new BusinessException("Failed to count requests by type: " + e.getMessage());
    }
  }

  public Integer countTotalRequests() {
    try {
      return Math.toIntExact(requestRepository.count());
    } catch (Exception e) {
      logger.error("Failed to count total requests: {}", e.getMessage());
      throw new BusinessException("Failed to count total requests: " + e.getMessage());
    }
  }
}
