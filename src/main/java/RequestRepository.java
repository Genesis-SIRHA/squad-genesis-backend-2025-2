import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestRepository {
    private List<Request> requests;

    public RequestRepository() {

    }

    public RequestRepository(String managedBy) {}

    public List<Request> findByStudentId(String userId) {
        return requests.stream()
                .filter(request -> userId.equals(request.getStudentId()))
                .collect(Collectors.toList());
    }

    public List<Request> findAllAvailable() {
        return ;
    }

    public List<Request> findAllPending(String userId) {
        return List.of();
    }

    public List<Request> queryExceptionionalRequest() {
        return ;
    }
    public Request findById(String id) {

        Optional<Request> foundRequest = requests.stream()
                .filter(request -> id.equals(request.getId()))
                .findFirst();

        return foundRequest.orElse(null);
    }

    public void update(String requestID, Request request ){

    }
}
