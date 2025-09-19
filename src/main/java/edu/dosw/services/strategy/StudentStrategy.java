package edu.dosw.services.strategy;

import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;

import java.util.List;

public class StudentStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;

    public StudentStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> queryRequests(String userId) {
        return requestRepository.findByStudentId(userId);
    }
}
