package edu.dosw.services;

import edu.dosw.model.Session;
import edu.dosw.repositories.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class that handles business logic related to academic sessions.
 * Provides methods for retrieving session information by group code.
 */
@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    /**
     * Constructs a new SessionService with the specified repository.
     *
     * @param sessionRepository the repository for session data access
     */
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * Retrieves all sessions associated with a specific group code.
     *
     * @param groupCode the unique code of the group
     * @return an List of Session objects for the specified group
     */
    public List<Session> getSessionsByGroupCode(String groupCode) {
        return sessionRepository.findByGroupCode(groupCode);
    }


}
