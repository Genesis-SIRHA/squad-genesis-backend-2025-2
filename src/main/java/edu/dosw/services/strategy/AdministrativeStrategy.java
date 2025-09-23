package edu.dosw.services.strategy;

import edu.dosw.model.Request;
import edu.dosw.repositories.RequestRepository;
import edu.dosw.services.MembersService;

import java.util.List;

public class AdministrativeStrategy implements QueryStrategy {
    private final RequestRepository requestRepository;
    private final MembersService membersService;

    public AdministrativeStrategy(RequestRepository requestRepository, MembersService membersService) {
        this.requestRepository = requestRepository;
        this.membersService = membersService;
    }

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
