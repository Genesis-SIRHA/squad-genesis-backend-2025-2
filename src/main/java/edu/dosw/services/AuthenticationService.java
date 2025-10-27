package edu.dosw.services;

import edu.dosw.dto.AuthResponseDto;
import edu.dosw.dto.LogInDTO;
import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.dto.UserInfoDto;
import edu.dosw.exception.BusinessException;
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

  public AuthResponseDto logIn(LogInDTO logInDTO) {
    String email = logInDTO.email().toLowerCase();
    if (validateUserEmail(email)) {
      logger.error("Invalid email");
      throw new BusinessException("Invalid email");
    }
    Optional<UserCredentialsDto> userCredentials = this.getByEmail(email);
    if (!verifyPassword(logInDTO.password(), userCredentials.get().password())) {
      logger.error("Invalid password");
      throw new BusinessException("Invalid password");
    }
    String token = jwtUtil.generateToken(userCredentials.get().userId(), email);
    UserCredentialsDto user = userCredentials.get();
    UserInfoDto userInfo = new UserInfoDto(user.userId(), user.email(), user.role());
    return new AuthResponseDto(token, userInfo);
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

  public Optional<UserCredentialsDto> getByEmail(String email) {
    Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByEmail(email);
    if (userCredentials.isEmpty()) {
      logger.error("User not found");
      throw new BusinessException("User not found");
    }
    return userCredentials;
  }

  public Optional<UserCredentialsDto> getByUserId(String id) {
    Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByUserId(id);
    if (userCredentials.isEmpty()) {
      logger.error("User not found");
      throw new BusinessException("User not found");
    }
    return userCredentials;
  }

  public void createAuthentication(UserInfoDto userInfoDto) {
    UserCredentialsDto userCredentialsDto =
        new UserCredentialsDto(
            UUID.randomUUID().toString(),
            userInfoDto.userId(),
            userInfoDto.email(),
            encryptPassword(userInfoDto.userId()),
            userInfoDto.role());
    userCredentialsRepository.save(userCredentialsDto);
  }

  public UserInfoDto getUserInfo(String email) {
    Optional<UserCredentialsDto> userCredentials = this.getByEmail(email);
    UserCredentialsDto user = userCredentials.get();
    return new UserInfoDto(user.userId(), user.email(), user.role());
  }

  public void deleteAuthentication(User user) {
    Optional<UserCredentialsDto> userCredentialsDto = this.getByEmail(user.getEmail());
    userCredentialsRepository.delete(userCredentialsDto.get());
  }
}
