package services.strategy;

import model.Request;

import java.util.List;

public interface QueryStrategy {
    List<Request> queryRequests(String userId);
}
