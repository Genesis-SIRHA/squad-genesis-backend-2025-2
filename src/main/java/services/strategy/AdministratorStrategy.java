package services.strategy;

import model.Request;
import repositories.RequestRepository;

import java.util.List;

public class AdministratorStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;
    public AdministratorStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    public List<Request> queryRequests(String userId) {
        return requestRepository.queryExceptionRequest();
    }
}
