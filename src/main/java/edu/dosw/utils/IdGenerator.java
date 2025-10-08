package edu.dosw.utils;

import edu.dosw.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class IdGenerator {
    private static final SecureRandom SR = new SecureRandom();
    private static AuthenticationService authenticationService;

    @Autowired
    public IdGenerator(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public String generateUniqueId() {
        long n = Math.floorMod(SR.nextLong(), 10_000_000_000L);
        String id = String.format("%010d", n);
        try {
            if (authenticationService.findByUserId(id).isPresent()) {
                return generateUniqueId();
            }
        }catch (Exception e){
            return id;
        }
        return id;
    }
}