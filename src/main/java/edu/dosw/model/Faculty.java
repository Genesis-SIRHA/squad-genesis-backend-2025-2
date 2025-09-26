package edu.dosw.model;

import java.util.ArrayList;

public class Faculty {
    private String facultyName;
    private String plan;
    private ArrayList<Course> courses;

    public Faculty(String facultyName, String plan, ArrayList<Course> courses) {
        this.facultyName = facultyName;
        this.plan = plan;
        this.courses = courses;
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

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}
