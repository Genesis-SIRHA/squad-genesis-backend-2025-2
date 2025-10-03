package edu.dosw.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "universityMembers")
public class User {
  @Id private final String userId;
  private String type;
  private String name;
  private String plan;
  private String facultyName;

  public User(String userId, String type, String name, String plan, String facultyName) {
    this.userId = userId;
    this.type = type;
    this.name = name;
    this.plan = plan;
    this.facultyName = facultyName;
  }
}
