import java.util.List;

public class RequestRepository {

    public RequestRepository() {

    }

    public RequestRepository(String managedBy) {}

    public List<Request> findByStudentId(String userId) {
        return;
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
}
