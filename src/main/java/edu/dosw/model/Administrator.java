package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "administrators")
public class Administrator extends User {

  public Administrator(AdministratorBuilder builder) {
    super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
  }

  public static class AdministratorBuilder {
    private String userId;
    private String fullName;
    private String email;
    private String identityDocument;

    public AdministratorBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public AdministratorBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public AdministratorBuilder email(String email) {
      this.email = email;
      return this;
    }

    public AdministratorBuilder identityDocument(String identityDocument) {
      this.identityDocument = identityDocument;
      return this;
    }

    public Administrator build() {
      return new Administrator(this);
    }
  }
}
