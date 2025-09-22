package edu.dosw.services.strategy;

import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;

import java.util.List;

/**
 * Implementation of QueryStrategy for administrative users.
 * This strategy allows administrative users to see all available requests
 * as well as any requests they have created themselves.
 */
public class AdministrativeStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;

    /**
     * Constructs a new AdministrativeStrategy with the given request repository.
     * 
     * @param requestRepository The repository used to access request data
     */
    public AdministrativeStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Queries all available requests and those created by the specified user.
     * 
     * @param userId The ID of the administrative user
     * @return A combined list of all available requests and those created by the user
     */
    @Override
    public List<Request> queryRequests(String userId) {
        List<Request> result = requestRepository.findAllAvailable();
        result.addAll(requestRepository.findOwnedBy(userId));
        return result;
    }
}
