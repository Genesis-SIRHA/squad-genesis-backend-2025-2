package edu.dosw.services;

import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.exception.AuthenticationException;
import edu.dosw.model.User;
import edu.dosw.repositories.UserCredentialsRepository;
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
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthenticationService(UserCredentialsRepository userCredentialsRepository) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    public boolean logIn(UserCredentialsDto userCredentialsDto) {
        String email = userCredentialsDto.email().toLowerCase();
        if (validateUserEmail(email)) {
            logger.error("Invalid email");
            throw new AuthenticationException("Invalid email");
        }
        Optional<UserCredentialsDto> userCredentials = userCredentialsRepository.findByEmail(email);
        if (userCredentials.isEmpty()) {
            logger.error("User not found");
            throw new AuthenticationException("User not found");
        }
        if (!verifyPassword(userCredentialsDto.password(), userCredentials.get().password())) {
            logger.error("Invalid password");
            throw new AuthenticationException("Invalid password");
        }
        return true;
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
            throw new ResourceNotFoundException("User not found");
        }
        return userCredentials;
    }

    public void createAuthentication(User user) {
        String password = encryptPassword(user.getUserId());
        String id = UUID.randomUUID().toString();
        UserCredentialsDto userCredentialsDto =
                new UserCredentialsDto(id, user.getUserId(), user.getEmail(), password);
        userCredentialsRepository.save(userCredentialsDto);
    }

    public void deleteAuthentication(User user) {
        Optional<UserCredentialsDto> userCredentialsDto =
                userCredentialsRepository.findByEmail(user.getEmail());
        if (userCredentialsDto.isEmpty()) {
            logger.error("User not found");
            throw new ResourceNotFoundException("User not found");
        }
        userCredentialsRepository.delete(userCredentialsDto.get());
    }
}