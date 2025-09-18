import java.util.List;
import java.util.ArrayList;

public class AdministrativeStrategy implements QueryStrategy{
    private final RequestRepository requestRepository;

    public AdministrativeStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> queryRequests(String userId) {
        List<Request> result = requestRepository.findAllAvailable();
        result.addAll(requestRepository.findAllPending(userId));
        return result;
    }

}
