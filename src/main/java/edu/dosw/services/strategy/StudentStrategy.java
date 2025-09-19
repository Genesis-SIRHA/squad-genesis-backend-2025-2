package edu.dosw.services.strategy;

import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;

import java.util.List;

/**
 * Implementation of QueryStrategy for student users.
 * This strategy allows students to see only their own requests.
 */
public class StudentStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;

    /**
     * Constructs a new StudentStrategy with the given request repository.
     * 
     * @param requestRepository The repository used to access request data
     */
    public StudentStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Queries requests that belong to the specified student.
     * 
     * @param userId The ID of the student whose requests to retrieve
     * @return A list of requests created by the specified student
     */
    @Override
    public List<Request> queryRequests(String userId) {
        return requestRepository.findByStudentId(userId);
    }
}
