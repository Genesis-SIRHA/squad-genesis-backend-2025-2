package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "deans")
public class Dean extends User {
  private String facultyName;

  /** Represents a dean user in the system, associated with a specific faculty */
  public Dean(DeanBuilder builder) {
    super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
    this.facultyName = builder.facultyName;
  }

  /** Builder class for creating Dean instances */
  public static class DeanBuilder {
    private String userId;
    private String fullName;
    private String email;
    private String identityDocument;
    private String facultyName;

    /**
     * Sets the user ID for the dean
     *
     * @param userId The unique identifier for the dean
     * @return The DeanBuilder instance
     */
    public DeanBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    /**
     * Sets the full name for the dean
     *
     * @param fullName The full name of the dean
     * @return The DeanBuilder instance
     */
    public DeanBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    /**
     * Sets the email for the dean
     *
     * @param email The email address of the dean
     * @return The DeanBuilder instance
     */
    public DeanBuilder email(String email) {
      this.email = email;
      return this;
    }

    /**
     * Sets the identity document for the dean
     *
     * @param identityDocument The identity document number of the dean
     * @return The DeanBuilder instance
     */
    public DeanBuilder identityDocument(String identityDocument) {
      this.identityDocument = identityDocument;
      return this;
    }

    /**
     * Sets the faculty name for the dean
     *
     * @param facultyName The name of the faculty the dean is assigned to
     * @return The DeanBuilder instance
     */
    public DeanBuilder facultyName(String facultyName) {
      this.facultyName = facultyName;
      return this;
    }

    /**
     * Builds and returns a Dean instance
     *
     * @return A new Dean instance
     */
    public Dean build() {
      return new Dean(this);
    }
  }
}
