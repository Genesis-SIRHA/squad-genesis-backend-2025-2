package edu.dosw.services;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.Administrative;
import edu.dosw.repositories.AdministrativeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministrativeService {

    private final AdministrativeRepository administrativeRepository;
    private static final Logger logger = LoggerFactory.getLogger(MembersService.class);

    @Autowired
    public AdministrativeService(AdministrativeRepository administrativeRepository) {
        this.administrativeRepository = administrativeRepository;
    }
    /**
     * Retrieves the faculty fullName associated with a user.
     *
     * @param id the unique identifier of the user
     * @return the faculty fullName associated with the user
     * @throws BusinessException if no user is found with the given ID
     */
  public String getFaculty(String id) {
    Optional<Administrative> user = administrativeRepository.findById(id);
    if (user.isEmpty()) {
      logger.error("User not found with id: " + id);
      throw new BusinessException("User not found with id: " + id);
    }
    return user.get().getFacultyName();
  }
}
