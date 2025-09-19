package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import services.strategy.AdministrativeStrategy;
import services.strategy.AdministratorStrategy;
import services.strategy.QueryStrategy;
import services.strategy.StudentStrategy;

import repositories.Wuwu;
import model.Request;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {
    
    private final Wuwu wuwu;
    private final Map<String, QueryStrategy> strategyMap;

    @Autowired
    public RequestService(Wuwu wuwu) {
        this.wuwu = wuwu;
        this.strategyMap = Map.of(
            "STUDENT", new StudentStrategy(wuwu),
            "ADMINISTRATIVE", new AdministrativeStrategy(wuwu),
            "ADMINISTRATOR", new AdministratorStrategy(wuwu)
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
    

    public Request createRequest(Request request) {
        // Asegurarse de que las fechas estén establecidas
        request.setCreatedAt(new Date());
        request.setUpdatedAt(new Date());
        return wuwu.save(request);
    }

    public Request updateRequestStatus(String id, String status) {
        return wuwu.findById(id)
            .map(request -> {
                request.setStatus(status);
                request.setUpdatedAt(new Date());
                return wuwu.save(request);
            })
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }


    public RequestStats getRequestStats() {
        long total = wuwu.count();
        long pending = wuwu.countByStatus("PENDING");
        long approved = wuwu.countByStatus("APPROVED");
        long rejected = wuwu.countByStatus("REJECTED");
        
        return new RequestStats(total, pending, approved, rejected);
    }

    public List<Request> getRecentRequests() {
        Date oneWeekAgo = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        return wuwu.findRecentRequests(oneWeekAgo);
    }

    public static record RequestStats(
            long total,
            long pending,
            long approved,
            long rejected
    ) {}
}
