package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "professors")
public class Professor extends User {
  private String facultyName;

  /**
   * Constructs a Professor using the builder pattern
   *
   * @param builder The ProfessorBuilder containing professor data
   */
  public Professor(ProfessorBuilder builder) {
    super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
    this.facultyName = builder.facultyName;
  }

  /** Builder class for creating Professor instances */
  public static class ProfessorBuilder {
    private String userId;
    private String fullName;
    private String email;
    private String identityDocument;
    private String facultyName;

    public ProfessorBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public ProfessorBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public ProfessorBuilder email(String email) {
      this.email = email;
      return this;
    }

    public ProfessorBuilder identityDocument(String identityDocument) {
      this.identityDocument = identityDocument;
      return this;
    }

    public ProfessorBuilder facultyName(String facultyName) {
      this.facultyName = facultyName;
      return this;
    }

    public Professor build() {
      return new Professor(this);
    }
  }
}
