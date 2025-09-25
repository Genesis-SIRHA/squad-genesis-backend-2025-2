package edu.dosw.services;

import edu.dosw.model.Session;
import edu.dosw.repositories.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SessionService {
    private SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public ArrayList<Session> getSessionsByAbbreviationAndGroup(String abbreviation, String groupCode) {
        return sessionRepository.findByAbbreviationAndGroupCode(abbreviation, groupCode);
    }

}
