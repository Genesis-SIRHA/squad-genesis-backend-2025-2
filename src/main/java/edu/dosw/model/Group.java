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

    public Group(String groupCode, String professor, int maxCapacity, int enrolled) {
        this.groupCode = groupCode;
        this.teacherId = professor;
        this.maxCapacity = maxCapacity;
        this.enrolled = enrolled;
    }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public int getmaxCapacity() { return maxCapacity; }

    public void setmaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public int getEnrolled() { return enrolled; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }
}
