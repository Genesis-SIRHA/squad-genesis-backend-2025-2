package edu.dosw.dto;

/**
 * Data Transfer Object for authentication response
 *
 * @param token The JWT token for authenticated sessions
 * @param user The user information of the authenticated user
 */
public record AuthResponseDto(String token, UserInfoDto user) {}
