package edu.dosw.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Member{
    private String memberId;
    private String type;
    private String name;
    private String plan;
    private String facultyId;

    public Member(String memberId, String name, String type, String facultyId) {
        this.memberId = memberId;
        this.name = name;
        this.type = type;
        this.facultyId = facultyId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getType() {
        return type;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
