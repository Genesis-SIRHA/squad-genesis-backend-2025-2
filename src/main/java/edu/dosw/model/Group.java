package edu.dosw.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "groups")
public class Group {
    private String groupCode;
    private String abbreviation;
    private String year;
    private String semester;
    private String teacherId;
    private boolean isLab;
    private int groupNum;
    private int enrolled;
    private int maxCapacity;

    public Group() {}

    public Group(String groupCode, String professor, int capacity, int enrolled) {
        this.groupCode = groupCode;
        this.teacherId = professor;
        this.capacity = capacity;
        this.enrolled = enrolled;
    }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getEnrolled() { return enrolled; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }
}
