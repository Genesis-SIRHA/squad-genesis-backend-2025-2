package edu.dosw.services;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.User;
import edu.dosw.repositories.MembersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class that handles business logic related to user members. Provides methods for
 * retrieving user information and faculty associations.
 */
@Service
public class MembersService {
  private final MembersRepository membersRepository;
  private static final Logger logger = LoggerFactory.getLogger(MembersService.class);


  @Autowired
  public MembersService(MembersRepository membersRepository) {
    this.membersRepository = membersRepository;
  }

  /**
   * Retrieves a user by their unique identifier.
   *
   * @param id the unique identifier of the user to retrieve
   * @return the user entity if found
   * @throws BusinessException if no user is found with the given ID
   */
  public User listById(String id) {
    return membersRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException("User not found with id: " + id));
  }

  /**
   * Retrieves the faculty name associated with a user.
   *
   * @param id the unique identifier of the user
   * @return the faculty name associated with the user
   * @throws BusinessException if no user is found with the given ID
   */
  public String getFaculty(String id) {
    Optional<User> user = membersRepository.findByUserId(id);
    if(user.isEmpty()){
      logger.error("User not found with id: " + id);
      throw new BusinessException("User not found with id: " + id);
    }
    return user.get().getFacultyName();
  }

  public String getPlan(String studentId) {
    Optional<User> user = membersRepository.findByUserId(studentId);
    if (user.isEmpty()){
      logger.error("Student not found with id: "+ studentId);
      throw new BusinessException("Student not found with id: "+ studentId);
    }
    return user.get().getPlan();
  }
}
