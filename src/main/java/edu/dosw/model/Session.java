package edu.dosw.model;

import java.time.DayOfWeek;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sessions")
public class Session {
  private String groupCode;
  private String classroomName;
  private int slot;
  private DayOfWeek day;
  private int year;
  private int period;

  public Session() {}

  public Session(
      String groupCode, String classroomName, int slot, DayOfWeek day, int year, int period) {
    this.groupCode = groupCode;
    this.classroomName = classroomName;
    this.slot = slot;
    this.day = day;
    this.year = year;
    this.period = period;
  }

  // Getters and Setters
  public String getGroupCode() {
    return groupCode;
  }

  public void setGroupCode(String groupCode) {
    this.groupCode = groupCode;
  }

  public String getClassroomName() {
    return classroomName;
  }

  public void setClassroomName(String classroomName) {
    this.classroomName = classroomName;
  }

  public int getSlot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public DayOfWeek getDay() {
    return day;
  }

  public void setDay(DayOfWeek day) {
    this.day = day;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getPeriod() {
    return period;
  }

  public void setPeriod(int period) {
    this.period = period;
  }
}
