package edu.dosw.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "universityMembers")
public class User {
  @Id protected String id;
  protected String userId;
  protected String identityDocument;
  protected String email;
  protected String fullName;

  /** Default constructor that generates a unique user ID */
  public User() {
    this.userId = UUID.randomUUID().toString();
  }

  /**
   * Constructs a User with specified details
   *
   * @param userId The unique identifier for the user
   * @param identityDocument The identity document number
   * @param email The email address of the user
   * @param fullName The full name of the user
   */
  public User(String userId, String identityDocument, String email, String fullName) {
    this.userId = userId;
    this.identityDocument = identityDocument;
    this.email = email;
    this.fullName = fullName;
  }
}
