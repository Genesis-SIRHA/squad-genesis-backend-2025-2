package services.strategy;

import java.util.List;

public interface QueryStrategy {
    List<Request> queryRequests(String userId);
}
