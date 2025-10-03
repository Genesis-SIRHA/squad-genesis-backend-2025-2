package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "universityMembers")
public class User {
  @Id private final String userId;
  private String identityDocument;
  private String email;
  private String fullName;

  public User() {
      this.userId = UUID.randomUUID().toString();
  }

  public User(String identityDocument, String email,  String fullName) {
    this.userId = UUID.randomUUID().toString();
    this.identityDocument = identityDocument;
    this.email = email;
    this.fullName = fullName;
  }
}
