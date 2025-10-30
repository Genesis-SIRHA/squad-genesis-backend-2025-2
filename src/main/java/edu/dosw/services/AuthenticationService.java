package edu.dosw.services;

import edu.dosw.dto.AuthResponseDto;
import edu.dosw.dto.LogInDTO;
import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.AuthenticationException;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.User;
import edu.dosw.model.enums.Role;
import edu.dosw.repositories.UserCredentialsRepository;
import edu.dosw.utils.JwtUtil;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private final UserCredentialsRepository userCredentialsRepository;
  private final JwtUtil jwtUtil;
  private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * Constructs AuthenticationService with required dependencies
   *
   * @param userCredentialsRepository Repository for user credentials data access
   * @param jwtUtil Utility for JWT token operations
   */
  @Autowired
  public AuthenticationService(
      UserCredentialsRepository userCredentialsRepository, JwtUtil jwtUtil) {
    this.userCredentialsRepository = userCredentialsRepository;
    this.jwtUtil = jwtUtil;
  }

  /**
   * Authenticates a user and returns JWT token upon successful login
   *
   * @param logInDTO The login credentials containing email and password
   * @return AuthResponseDto containing JWT token and user information
   * @throws AuthenticationException If authentication fails due to invalid credentials
   */
  public AuthResponseDto logIn(LogInDTO logInDTO) {
    String email = logInDTO.email().toLowerCase();
    if (validateUserEmail(email)) {
      logger.error("Invalid email");
      throw new AuthenticationException("Invalid email");
    }
    Optional<UserCredentialsDto> userCredentials = this.getByEmail(email);
    if (!verifyPassword(logInDTO.password(), userCredentials.get().password())) {
      logger.error("Invalid password");
      throw new AuthenticationException("Invalid password");
    }
    String token = jwtUtil.generateToken(userCredentials.get().userId(), email);
    UserCredentialsDto user = userCredentials.get();
    UserInfoDto userInfo = new UserInfoDto(user.userId(), user.email(), user.role(), user.pfpURL());
    return new AuthResponseDto(token, userInfo);
  }

  /**
   * Validates user email format against institutional pattern
   *
   * @param email The email to validate
   * @return true if email is invalid, false if valid
   */
  private boolean validateUserEmail(String email) {
    return !email.matches("^[A-Za-z0-9+_.-]+@mail\\.escuelaing\\.edu\\.co$");
  }

  /**
   * Encrypts a plain text password using BCrypt
   *
   * @param password The plain text password to encrypt
   * @return The encrypted password
   */
  public String encryptPassword(String password) {
    return passwordEncoder.encode(password);
  }

  /**
   * Verifies if a raw password matches the encrypted password
   *
   * @param rawPassword The plain text password
   * @param encodedPassword The encrypted password to compare against
   * @return true if passwords match, false otherwise
   */
  public boolean verifyPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  /**
   * Retrieves user credentials by email
   *
   * @param email The email to search for
   * @return Optional containing UserCredentialsDto if found
   * @throws BusinessException If user is not found
   */
  public Optional<UserCredentialsDto> getByEmail(String email) {
    Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByEmail(email);
    if (userCredentials.isEmpty()) {
      logger.error("User not found");
      throw new BusinessException("User not found");
    }
    return userCredentials;
  }

  /**
   * Retrieves user credentials by user ID
   *
   * @param id The user ID to search for
   * @return Optional containing UserCredentialsDto if found
   * @throws ResourceNotFoundException If user is not found
   */
  public Optional<UserCredentialsDto> getByUserId(String id) {
    Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByUserId(id);
    if (userCredentials.isEmpty()) {
      logger.error("User not found");
      throw new ResourceNotFoundException("User not found");
    }
    return userCredentials;
  }

  /**
   * Creates authentication credentials for a new user
   *
   * @param userInfoDto The user information to create credentials for
   */
  public void createAuthentication(UserInfoDto userInfoDto) {
    UserCredentialsDto userCredentialsDto =
        new UserCredentialsDto(
            UUID.randomUUID().toString(),
            userInfoDto.userId(),
            userInfoDto.email(),
            encryptPassword(userInfoDto.userId()),
            userInfoDto.role(),
            "pfpURL");
    userCredentialsRepository.save(userCredentialsDto);
  }

  /**
   * Retrieves user information by email
   *
   * @param email The email to search for
   * @return UserInfoDto containing user information
   */
  public UserInfoDto getUserInfo(String email) {
    Optional<UserCredentialsDto> userCredentials = this.getByEmail(email);
    UserCredentialsDto user = userCredentials.get();
    return new UserInfoDto(user.userId(), user.email(), user.role(), user.pfpURL());
  }

  /**
   * Deletes authentication credentials for a user
   *
   * @param user The user whose credentials should be deleted
   */
  public void deleteAuthentication(User user) {
    Optional<UserCredentialsDto> userCredentialsDto = this.getByEmail(user.getEmail());
    userCredentialsRepository.delete(userCredentialsDto.get());
  }

  /**
   * Checks if the current user can access student data based on role and ownership
   *
   * @param authentication The current authentication context
   * @param studentId The student ID being accessed
   * @return true if access is allowed, false otherwise
   */
  public boolean canAccessStudentData(Authentication authentication, String studentId) {
    String currentUserEmail = authentication.getName();
    UserInfoDto currentUser = getUserInfo(currentUserEmail);

    if (currentUser.role() == Role.STUDENT) {
      return currentUser.userId().equals(studentId);
    }

    return true;
  }

  /**
   * Checks if the current user can access user data based on role and ownership
   *
   * @param authentication The current authentication context
   * @param userId The user ID being accessed
   * @return true if access is allowed, false otherwise
   */
  public boolean canAccessUserData(Authentication authentication, String userId) {
    String currentUserEmail = authentication.getName();
    UserInfoDto currentUser = getUserInfo(currentUserEmail);

    if (currentUser.role() == Role.STUDENT || currentUser.role() == Role.PROFESSOR) {
      return currentUser.userId().equals(userId);
    }

    return true;
  }

  /**
   * Checks if the current user can access a specific request
   *
   * @param authentication The current authentication context
   * @param requestId The request ID being accessed
   * @return true if access is allowed, false otherwise
   */
  public boolean canAccessUserRequest(Authentication authentication, String requestId) {
    String currentUserEmail = authentication.getName();
    UserInfoDto currentUser = getUserInfo(currentUserEmail);

    if (currentUser.role() == Role.STUDENT) {
      logger.warn("Student request ownership check not yet implemented for request: {}", requestId);
      return true;
    }

    return true;
  }

  /**
   * Gets the current user's ID from authentication context
   *
   * @param authentication The current authentication context
   * @return The current user's ID
   */
  public String getCurrentUserId(Authentication authentication) {
    String currentUserEmail = authentication.getName();
    UserInfoDto currentUser = getUserInfo(currentUserEmail);
    return currentUser.userId();
  }

  /**
   * Gets the current user's role from authentication context
   *
   * @param authentication The current authentication context
   * @return The current user's role
   */
  public Role getCurrentUserRole(Authentication authentication) {
    String currentUserEmail = authentication.getName();
    UserInfoDto currentUser = getUserInfo(currentUserEmail);
    return currentUser.role();
  }
}
