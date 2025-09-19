package services;

import dto.RequestDTO;
import dto.RequestResponse;
import dto.RequestStats;
import model.RequestDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.CourseRepository;
import services.strategy.AdministrativeStrategy;
import services.strategy.AdministratorStrategy;
import services.strategy.QueryStrategy;
import services.strategy.StudentStrategy;

import repositories.RequestRepository;
import model.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {
    
    private final RequestRepository requestRepository;
    private final CourseRepository courseRepository ;
    private final Map<String, QueryStrategy> strategyMap;

    @Autowired
    public RequestService(RequestRepository requestRepository,CourseRepository courseRepository) {
        this.requestRepository = requestRepository;
        this.courseRepository = courseRepository;
        this.strategyMap = Map.of(
            "STUDENT", new StudentStrategy(requestRepository),
            "ADMINISTRATIVE", new AdministrativeStrategy(requestRepository),
            "ADMINISTRATOR", new AdministratorStrategy(requestRepository)
        );
    }

    public List<Request> fetchRequests(String role, String userId) {
        QueryStrategy strategy = strategyMap.get(role.toUpperCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported role: " + role);
        }

        return strategy.queryRequests(userId).stream()
            .sorted(Comparator.comparing(Request::getCreatedAt).reversed())
            .toList();
    }
    

    public RequestResponse createRequest(RequestDTO requestDTO) {

        Request request = new Request();
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus("PENDING");
        request.setType(requestDTO.type());
        request.setStudentId(requestDTO.studentId());
        request.setIsExceptional(requestDTO.isExceptional());

        request.setAnswerAt(LocalDate.now());
        request.setManagedBy(requestDTO.studentId());
        request.setAnswer(requestDTO.description());
        request.setAnswerDate(LocalDate.now());

        ;
        request.setOriginGroup(courseRepository.findByCode(requestDTO.originGroupId()));
        request.setDestinationGroup(courseRepository.findByCode(requestDTO.destinationGroupId()));

        return RequestResponse.fromRequest(requestRepository.save(request));
    }

    public Request updateRequestStatus(String id, String status) {
        return requestRepository.findById(id)
            .map(request -> {
                request.setStatus(status);
                return requestRepository.save(request);
            })
            .orElseThrow(() -> new RuntimeException("Request not found with id: " + id));
    }


    public RequestStats getRequestStats() {
        long total = requestRepository.count();
        long pending = requestRepository.countByStatus("PENDING");
        long approved = requestRepository.countByStatus("APPROVED");
        long rejected = requestRepository.countByStatus("REJECTED");
        return new RequestStats(total, pending, approved, rejected);
    }

    public Request respondToRequest(String requestId, RequestDetails responseDetails) {
        Request request = requestRepository.findById(requestId).orElse(null);

        if (request != null) {
            if (request.getRequestDetails() != null) {
                RequestDetails existingDetails = request.getRequestDetails();
                existingDetails.setAnswerDate(LocalDate.now());
                existingDetails.setManagedBy(responseDetails.getManagedBy());
                existingDetails.setAnswer(responseDetails.getAnswer());
            } else {
                responseDetails.setRequestId(requestId);
                request.setRequestDetails(responseDetails);
            }

            request.setStatus(responseDetails.getAnswer());
            return requestRepository.save(request);
        }
        return null;
    }

}
