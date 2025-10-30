package edu.dosw.dto;

import edu.dosw.model.enums.Role;

/**
 * Data Transfer Object for user information
 *
 * @param userId The unique identifier of the user
 * @param email The user's email address
 * @param role The role of the user in the system
 * @param pfpURL The URL to the user's profile picture
 */
public record UserInfoDto(String userId, String email, Role role, String pfpURL) {}
