package edu.dosw.model;

import edu.dosw.model.enums.AcademicGrade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "students")
public class Student extends User {
  private String plan;
  private String facultyName;
  private Integer generalAverage;
  private Integer semester;
  private AcademicGrade academicGrade;

  public Student(StudentBuilder builder) {
    super(builder.userId, builder.identityDocument, builder.email, builder.fullName);
    this.plan = builder.plan;
    this.facultyName = builder.facultyName;
    this.academicGrade = builder.academicGrade;
    this.generalAverage = builder.generalAverage;
    this.semester = builder.semester;
  }

  public static class StudentBuilder {
    public Integer semester;
    private String userId;
    private String fullName;
    private String email;
    private String identityDocument;
    private String plan;
    private String facultyName;
    private int generalAverage;
    private AcademicGrade academicGrade;

    public StudentBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public StudentBuilder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public StudentBuilder email(String email) {
      this.email = email;
      return this;
    }

    public StudentBuilder identityDocument(String identityDocument) {
      this.identityDocument = identityDocument;
      return this;
    }

    public StudentBuilder plan(String plan) {
      this.plan = plan;
      return this;
    }

    public StudentBuilder facultyName(String facultyName) {
      this.facultyName = facultyName;
      return this;
    }

    public StudentBuilder generalAverage(Integer generalAverage) {
      this.generalAverage = generalAverage;
      return this;
    }

    public StudentBuilder semester(Integer semester) {
      this.semester = semester;
      return this;
    }

    public StudentBuilder academicGrade(AcademicGrade academicGrade) {
      this.academicGrade = academicGrade;
      return this;
    }

    public Student build() {
      return new Student(this);
    }
  }
}
