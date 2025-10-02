package edu.dosw.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "groups")
public class Group {
  private String groupCode;
  private String abbreviation;
  private String year;
  private String period;
  private String teacherId;
  private boolean isLab;
  private int groupNum;
  private int enrolled;
  private int maxCapacity;

  public Group() {}

  public String getGroupCode() {
    return groupCode;
  }

  public void setGroupCode(String groupCode) {
    this.groupCode = groupCode;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getPeriod() {
    return period;
  }

  public void setPeriod(String period) {
    this.period = period;
  }

  public String getTeacherId() {
    return teacherId;
  }

  public void setTeacherId(String teacherId) {
    this.teacherId = teacherId;
  }

  public boolean isLab() {
    return isLab;
  }

  public void setLab(boolean lab) {
    isLab = lab;
  }

  public int getGroupNum() {
    return groupNum;
  }

  public void setGroupNum(int groupNum) {
    this.groupNum = groupNum;
  }

  public int getEnrolled() {
    return enrolled;
  }

  public void setEnrolled(int enrolled) {
    this.enrolled = enrolled;
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  public void setMaxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }
}
