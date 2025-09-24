package edu.dosw.services.strategy;

import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;

import java.util.List;

/**
 * Implementation of QueryStrategy for administrator users.
 * This strategy allows administrators to see all exception requests
 * that require administrative review or approval.
 */
public class AdministratorStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;
    
    /**
     * Constructs a new AdministratorStrategy with the given request repository.
     * 
     * @param requestRepository The repository used to access request data
     */
    public AdministratorStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    
    /**
     * Queries all exception requests that require administrative attention.
     * 
     * @param userId The ID of the administrator (not used in this implementation)
     * @return A list of exception requests requiring administrative review
     */
    @Override
    public List<Request> queryRequests(String userId) {
        return requestRepository.queryExceptionRequest();
    }
}
