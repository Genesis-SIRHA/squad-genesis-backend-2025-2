package edu.dosw.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

  // Getters
  public String getUserId() {
    return userId;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getPlan() {
    return plan;
  }

  public String getFacultyName() {
    return facultyName;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlan(String plan) {
    this.plan = plan;
  }

  public void setFacultyName(String facultyName) {
    this.facultyName = facultyName;
  }
}
