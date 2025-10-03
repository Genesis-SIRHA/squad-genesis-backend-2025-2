package edu.dosw.services;

import edu.dosw.dto.UserCredentialsDto;
import edu.dosw.exception.BusinessException;
import edu.dosw.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean logIn(UserCredentialsDto userCredentialsDto){
        String email = userCredentialsDto.email().toLowerCase();
        if(validateUserEmail(email)){
            logger.error("Invalid email");
            throw new BusinessException("Invalid email");
        }
        Optional<UserCredentialsDto> userCredentials = userRepository.findByEmail(userCredentialsDto.email());
        if(userCredentials.isEmpty()){
            logger.error("User not found");
            throw new BusinessException("User not found");
        }
        if(!verifyPassword(userCredentialsDto.password(), userCredentials.get().password())){
            logger.error("Invalid password");
            throw new BusinessException("Invalid password");
        }
        return true;
    }

    private boolean validateUserEmail(String email){
        return !email.matches("^[A-Za-z0-9+_.-]+@mail\\.escuelaing\\.edu\\.co$");
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
