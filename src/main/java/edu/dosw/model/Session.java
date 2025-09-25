package edu.dosw.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "schedules")
public class Session {
    private String groupCode;
    private String abbreviation;
    private String classroomName;
    private int slot;
    private DayOfWeek day;
    private int year;
    private int semester;

    public String getId() {
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
        if (slot < 1 || slot > 7) {
            throw new IllegalArgumentException("Time slot must be between 1 and 7");
        }
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

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getGroupCode() {
        return groupCode;
    }
}
