package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "groups")
public class Group {
    @Id
    private String id;
  private String groupCode;
  private String abbreviation;
  private String year;
  private String period;
  private String professorId;
  private boolean isLab;
  private String groupNum;
  private int enrolled;
  private int maxCapacity;

  public Group(GroupBuilder builder) {
    this.groupCode = builder.groupCode;
    this.abbreviation = builder.abbreviation;
    this.year = builder.year;
    this.period = builder.period;
    this.professorId = builder.professorId;
    this.isLab = builder.isLab;
    this.groupNum = builder.groupNum;
    this.enrolled = builder.enrolled;
    this.maxCapacity = builder.maxCapacity;
  }

  public static class GroupBuilder {
      private String groupCode;
      private String abbreviation;
      private String year;
      private String period;
      private String professorId;
      private boolean isLab;
      private String groupNum;
      private int enrolled;
      private int maxCapacity;

      public GroupBuilder groupCode(String groupCode) {
          this.groupCode = groupCode.toUpperCase();
          return this;
      }

      public GroupBuilder abbreviation(String abbreviation) {
          this.abbreviation = abbreviation.toUpperCase();
          return this;
      }

      public GroupBuilder year(String year) {
          this.year = year;
          return this;
      }

      public GroupBuilder period(String period) {
          this.period = period;
          return this;
      }

      public GroupBuilder professorId(String professorId) {
          this.professorId = professorId;
          return this;
      }

      public GroupBuilder isLab(boolean isLab) {
          this.isLab = isLab;
          return this;
      }

      public GroupBuilder groupNum(String groupNum) {
          this.groupNum = groupNum;
          return this;
      }

      public GroupBuilder enrolled(int enrolled) {
          this.enrolled = enrolled;
          return this;
      }

      public GroupBuilder maxCapacity(int maxCapacity) {
          this.maxCapacity = maxCapacity;
          return this;
      }

      public Group build() {
          return new Group(this);
      }
  }
}
