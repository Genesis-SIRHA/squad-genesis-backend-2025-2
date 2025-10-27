package edu.dosw.dto;

import edu.dosw.model.enums.Role;

public record UserInfoDto(String userId, String email, Role role) {}
