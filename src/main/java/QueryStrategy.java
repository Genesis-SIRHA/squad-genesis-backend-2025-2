import java.util.List;

public interface QueryStrategy {
    List<Request> queryRequests(String userId);
}
