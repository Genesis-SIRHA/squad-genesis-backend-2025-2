import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RequestService {
    private final Map<String, QueryStrategy> strategyMap;

    public RequestService(RequestRepository repository) {
        this.strategyMap = Map.of(
                "STUDENT", new StudentStrategy(repository),
                "ADMINISTRATIVe", new AdministratorStrategy(repository),
                "ADMINISTRATROR", new AdministrativeStrategy(repository)
        );
    }

    public List<Request> fetchRequests(String role,String userId) {
        QueryStrategy strategy = strategyMap.get(role);

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported role: " + role);
        }

        return strategy.queryRequests(userId).stream().sorted(Comparator.comparing(Request::getCreatedAt)).toList();
    }
}
