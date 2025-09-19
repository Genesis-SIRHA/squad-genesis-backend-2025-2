package services;

import dto.RequestResponse;
import dto.RequestStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.strategy.AdministrativeStrategy;
import services.strategy.AdministratorStrategy;
import services.strategy.QueryStrategy;
import services.strategy.StudentStrategy;

import repositories.RequestRepository;
import model.Request;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {
    
    private final RequestRepository requestRepository;
    private final Map<String, QueryStrategy> strategyMap;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
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
    

    public RequestResponse createRequest(Request request) {
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return RequestResponse.fromRequest(requestRepository.save(request));
    }

    public Request updateRequestStatus(String id, String status) {
        return requestRepository.findById(id)
            .map(request -> {
                request.setStatus(status);
                request.setUpdatedAt(LocalDateTime.now());
                return requestRepository.save(request);
            })
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }


    public RequestStats getRequestStats() {
        long total = requestRepository.count();
        long pending = requestRepository.countByStatus("PENDING");
        long approved = requestRepository.countByStatus("APPROVED");
        long rejected = requestRepository.countByStatus("REJECTED");
        
        return new RequestStats(total, pending, approved, rejected);
    }

}
