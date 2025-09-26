package edu.dosw.services;

import edu.dosw.model.User;
import edu.dosw.repositories.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to user members.
 * Provides methods for retrieving user information and faculty associations.
 */
@Service
public class MembersService {
    private final MembersRepository membersRepository;

    @Autowired
    public MembersService(MembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return the user entity if found
     * @throws RuntimeException if no user is found with the given ID
     */
    public User listById(String id) {
        return membersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Retrieves the faculty ID associated with a user.
     *
     * @param id the unique identifier of the user
     * @return the faculty ID associated with the user
     * @throws RuntimeException if no user is found with the given ID
     */
    public String getFaculty(String id) {
        return  membersRepository.findById(id).get().getFacultyId();
    }
}
