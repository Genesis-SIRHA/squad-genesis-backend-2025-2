package edu.dosw.services.strategy;

import edu.dosw.model.Request;

import java.util.List;

public interface QueryStrategy {
    List<Request> queryRequests(String userId);
}
