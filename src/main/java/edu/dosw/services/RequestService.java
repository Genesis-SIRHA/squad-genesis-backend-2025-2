package edu.dosw.services;

import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestResponse;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.dosw.repositories.CourseRepository;
import edu.dosw.services.strategy.AdministrativeStrategy;
import edu.dosw.services.strategy.AdministratorStrategy;
import edu.dosw.services.strategy.QueryStrategy;
import edu.dosw.services.strategy.StudentStrategy;

import edu.dosw.repositories.RequestRepository;
import edu.dosw.model.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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

        Group origin = courseRepository.findByCode(requestDTO.originGroupId());
        if (origin == null) {
            throw new IllegalArgumentException("Origin group not found: " + requestDTO.originGroupId());
        }

        Group destination = courseRepository.findByCode(requestDTO.destinationGroupId());
        if (destination == null) {
            throw new IllegalArgumentException("Destination group not found: " + requestDTO.destinationGroupId());
        }

        request.setOriginGroup(origin);
        request.setDestinationGroup(destination);

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
}
