import java.time.LocalDate;
import java.util.*;

public class RequestService {
    private final Map<String, QueryStrategy> strategyMap;
    private final RequestRepository repository; // Campo añadido

    public RequestService(RequestRepository repository) {
        this.repository = repository;
        this.strategyMap = Map.of(
                "STUDENT", new StudentStrategy(repository),
                "ADMINISTRATIVE", new AdministratorStrategy(repository),
                "ADMINISTRATOR", new AdministrativeStrategy(repository)
        );
    }

    public List<Request> fetchRequests(String role, String userId) {
        QueryStrategy strategy = strategyMap.get(role.toUpperCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported role: " + role);
        }

        return strategy.queryRequests(userId).stream()
                .sorted(Comparator.comparing(Request::getCreatedAt))
                .collect(java.util.stream.Collectors.toList());
    }

    public Request respondToRequest(String requestId, RequestDetails responseDetails) {
        Request request = repository.findById(requestId);

        if (request != null) {
            if (request.getRequestDetails() != null) {
                RequestDetails existingDetails = request.getRequestDetails();
                existingDetails.setAnswerAt(LocalDate.now());
                existingDetails.setManagedBy(responseDetails.getManagedBy());
                existingDetails.setAnswer(responseDetails.getAnswer());
            } else {
                responseDetails.setRequestId(requestId);
                responseDetails.setCreatedAt(new Date().toString());
                request.setRequestDetails(responseDetails);
            }

            request.setStatus(responseDetails.getAnswer());
            repository.update(requestId, request);
            responseDetails.insert();

            return request;
        }
        return null;
    }
}