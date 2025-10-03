package edu.dosw.dto;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usersCredentials")
public record UserCredentialsDto (
         String email,
         String password
){}
