package services.strategy;

import repositories.RequestRepository;

import java.util.List;

public class AdministrativeStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;

    public AdministrativeStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> queryRequests(String userId) {
        List<Request> result = requestRepository.findAllAvailable();
        result.addAll(requestRepository.findAllOwned(userId));
        return result;
    }

}
