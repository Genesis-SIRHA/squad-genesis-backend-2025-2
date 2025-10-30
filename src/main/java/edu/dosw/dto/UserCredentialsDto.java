package edu.dosw.dto;

import edu.dosw.model.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data Transfer Object for user credentials information
 *
 * @param id The unique identifier of the credentials record
 * @param userId The unique identifier of the user
 * @param email The user's email address
 * @param password The user's encrypted password
 * @param role The role of the user in the system
 * @param pfpURL The URL to the user's profile picture
 */
@Document(collection = "usersCredentials")
public record UserCredentialsDto(
    @Id String id, String userId, String email, String password, Role role, String pfpURL) {}
