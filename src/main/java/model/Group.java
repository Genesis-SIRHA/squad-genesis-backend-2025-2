package model;

import java.util.ArrayList;

public class Group {
    private String groupCode;
    private String professor;
    private int capacity;
    private int enrolled;
    private ArrayList<Session> schedule;

    public Group() {}

    public Group(String groupCode, String professor, int capacity, int enrolled) {
        this.groupCode = groupCode;
        this.professor = professor;
        this.capacity = capacity;
        this.enrolled = enrolled;
    }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getEnrolled() { return enrolled; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }
}
