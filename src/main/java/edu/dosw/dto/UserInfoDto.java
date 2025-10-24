package edu.dosw.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usersCredentials")
public record UserInfoDto(@Id String id, String userId, String email, String role) {}
