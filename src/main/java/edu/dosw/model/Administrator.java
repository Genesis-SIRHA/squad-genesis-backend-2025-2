package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "administrators")
public class Administrator extends User {
  /**
   * Constructs an Administrator using the builder pattern
   *
   * @param builder The AdministratorBuilder containing administrator data
   */
  public Administrator(AdministratorBuilder builder) {
    super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
  }

  /** Builder class for creating Administrator instances */
  public static class AdministratorBuilder {
    private String userId;
    private String fullName;
    private String email;
    private String identityDocument;

    /**
     * Sets the user ID for the administrator
     *
     * @param userId The unique identifier for the administrator
     * @return The AdministratorBuilder instance
     */
    public AdministratorBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    /**
     * Sets the full name for the administrator
     *
     * @param fullName The full name of the administrator
     * @return The AdministratorBuilder instance
     */
    public AdministratorBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    /**
     * Sets the email for the administrator
     *
     * @param email The email address of the administrator
     * @return The AdministratorBuilder instance
     */
    public AdministratorBuilder email(String email) {
      this.email = email;
      return this;
    }

    /**
     * Sets the identity document for the administrator
     *
     * @param identityDocument The identity document number of the administrator
     * @return The AdministratorBuilder instance
     */
    public AdministratorBuilder identityDocument(String identityDocument) {
      this.identityDocument = identityDocument;
      return this;
    }

    /**
     * Builds and returns an Administrator instance
     *
     * @return A new Administrator instance
     */
    public Administrator build() {
      return new Administrator(this);
    }
  }
}
