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
    public String getAbbreviation() { return abbreviation; }
    public String getYear() { return year; }
    public String getSemester() { return semester; }
    public String getTeacherId() { return teacherId; }
    public boolean isLab() { return isLab; }
    public int getGroupNum() { return groupNum; }
    public int getEnrolled() { return enrolled; }
    public int getMaxCapacity() { return maxCapacity; }

    public String getProfessor() { return teacherId; }
    public int getCapacity() { return maxCapacity; }




    public void setProfessor(String professor) {
        this.teacherId = professor;
    }

    public void setCapacity(int capacity) {
        this.maxCapacity = capacity;
    }

    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public void setYear(String year) { this.year = year; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public void setLab(boolean isLab) { this.isLab = isLab; }
    public void setGroupNum(int groupNum) { this.groupNum = groupNum; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }


}
