package edu.dosw.model;

import java.util.ArrayList;
import java.util.List;

public class Faculty {
  private String facultyName;
  private String plan;
  private List<Course> courses;

  public Faculty(String facultyName, String plan, List<Course> courses) {
    this.facultyName = facultyName;
    this.plan = plan;
    this.courses = new ArrayList<>(courses);
  }

  public String getFacultyName() {
    return facultyName;
  }

  public void setFacultyName(String facultyName) {
    this.facultyName = facultyName;
  }

  public String getPlan() {
    return plan;
  }

  public void setPlan(String plan) {
    this.plan = plan;
  }

  public List<Course> getCourses() {
    return courses;
  }

  public void setCourses(List<Course> courses) {
    this.courses = new ArrayList<>(courses);
  }
}
