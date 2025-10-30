package edu.dosw.dto;

/**
 * Data Transfer Object for user login credentials
 *
 * @param email The user's email address
 * @param password The user's password
 */
public record LogInDTO(String email, String password) {}
