package edu.dosw.services.strategy;

import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.MembersService;

import java.util.List;

/**
 * Implementation of QueryStrategy for administrative users.
 * This strategy allows administrative users to see all available requests
 * as well as any requests they have created themselves.
 */
public class AdministrativeStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;
    private final MembersService membersService;

    /**
     * Constructs a new AdministrativeStrategy with the given request repository.
     *
     * @param requestRepository The repository used to access request data
     * @param membersService the service that manages persons in the university
     */
    public AdministrativeStrategy(RequestRepository requestRepository, MembersService membersService) {
        this.requestRepository = requestRepository;
        this.membersService = membersService;
    }

    public AdministrativeStrategy(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
        this.membersService = null;
    }

    /**
     * Queries all available requests and those created by the specified user.
     *
     * @param userId The ID of the administrative user
     * @return A combined list of all available requests and those created by the user
     */
    @Override
    public List<Request> queryRequests(String userId) {
        String faculty = membersService.getFaculty(userId);

        if (faculty == null){
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        List<Request> result = requestRepository.findAllAvailable();
        result.addAll(requestRepository.findOwnedBy(userId, faculty));
        return result;
    }

}
