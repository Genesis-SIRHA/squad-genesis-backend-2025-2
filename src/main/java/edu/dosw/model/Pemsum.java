package edu.dosw.model;

import java.util.Map;

public class Pemsum {
    String studentId;
    String studentName;
    String facultyName;
    String facultyPlan;
    int approvedCredits;
    int totalCredits;
    Map<Course, String> courses;

    public Pemsum() {
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getFacultyPlan() {
        return facultyPlan;
    }

    public int getApprovedCredits() {
        return approvedCredits;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public Map<Course, String> getCourses() {
        return courses;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public void setFacultyPlan(String facultyPlan) {
        this.facultyPlan = facultyPlan;
    }

    public void setApprovedCredits(int approvedCredits) {
        this.approvedCredits = approvedCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public void setCourses(Map<Course, String> courses) {
        this.courses = courses;
    }
}
