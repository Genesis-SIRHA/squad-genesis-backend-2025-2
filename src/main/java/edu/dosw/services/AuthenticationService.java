package edu.dosw.services;

import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Administrator;
import edu.dosw.model.Dean;
import edu.dosw.model.Professor;
import edu.dosw.model.Student;
import edu.dosw.model.User;
import edu.dosw.repositories.UserCredentialsRepository;
import edu.dosw.utils.JwtUtil;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private final UserCredentialsRepository userCredentialsRepository;
  private final JwtUtil jwtUtil;
  private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public AuthenticationService(
      UserCredentialsRepository userCredentialsRepository, JwtUtil jwtUtil) {
    this.userCredentialsRepository = userCredentialsRepository;
    this.jwtUtil = jwtUtil;
  }

  public String logIn(UserCredentialsDto userCredentialsDto) {
    String email = userCredentialsDto.email().toLowerCase();
    if (validateUserEmail(email)) {
      logger.error("Invalid email");
      throw new BusinessException("Invalid email");
    }
    Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByEmail(email);
    if (userCredentials.isEmpty()) {
      logger.error("User not found");
      throw new BusinessException("User not found");
    }
    if (!verifyPassword(userCredentialsDto.password(), userCredentials.get().password())) {
      logger.error("Invalid password");
      throw new BusinessException("Invalid password");
    }
    return jwtUtil.generateToken(userCredentials.get().userId(), email);
  }

  private boolean validateUserEmail(String email) {
    return !email.matches("^[A-Za-z0-9+_.-]+@mail\\.escuelaing\\.edu\\.co$");
  }

  public String encryptPassword(String password) {
    return passwordEncoder.encode(password);
  }

  public boolean verifyPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public Optional<UserCredentialsDto> findByUserId(String id) {
    Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByUserId(id);
    if (userCredentials.isEmpty()) {
      logger.error("User not found");
      throw new BusinessException("User not found");
    }
    return userCredentials;
  }

  public void createAuthentication(User user) {
    String password = encryptPassword(user.getUserId());
    String id = UUID.randomUUID().toString();
    String role = determineUserRole(user);
    UserCredentialsDto userCredentialsDto =
        new UserCredentialsDto(id, user.getUserId(), user.getEmail(), password, role);
    userCredentialsRepository.save(userCredentialsDto);
  }

  private String determineUserRole(User user) {
    if (user instanceof Student) {
      return "STUDENT";
    } else if (user instanceof Professor) {
      return "PROFESSOR";
    } else if (user instanceof Administrator) {
      return "ADMINISTRATOR";
    } else if (user instanceof Dean) {
      return "DEAN";
    }
    return "USER";
  }

  public UserInfoDto getUserInfo(String email) {
    Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByEmail(email);
    if (userCredentials.isEmpty()) {
      throw new BusinessException("User not found");
    }

    UserCredentialsDto user = userCredentials.get();
    return new UserInfoDto(user.id(), user.userId(), user.email(), user.role());
  }

  public void deleteAuthentication(User user) {
    Optional<UserCredentialsDto> userCredentialsDto =
        userCredentialsRepository.findByEmail(user.getEmail());
    if (userCredentialsDto.isEmpty()) {
      logger.error("User not found");
      throw new BusinessException("User not found");
    }
    userCredentialsRepository.delete(userCredentialsDto.get());
  }
}
