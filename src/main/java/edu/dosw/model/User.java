package edu.dosw.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
public class User {
  @Id protected String id;
  protected String userId;
  protected String identityDocument;
  protected String email;
  protected String fullName;

  public User() {
    this.userId = UUID.randomUUID().toString();
  }

  public User(String userId, String identityDocument, String email, String fullName) {
    this.userId = userId;
    this.identityDocument = identityDocument;
    this.email = email;
    this.fullName = fullName;
  }
}
