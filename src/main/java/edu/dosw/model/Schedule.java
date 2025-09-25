package edu.dosw.model;

import java.util.ArrayList;

public class Schedule {
    private String studentId;
    private ArrayList<Session> sessions;

    public Schedule(String studentId, ArrayList<Session> sessions) {
        this.studentId = studentId;
        this.sessions = sessions;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public void deleteGroup(String abbreviation, String groupId) {
        sessions.removeIf(session -> session.getAbbreviation().equals(abbreviation) && session.getGroupCode().equals(groupId));
    }

    public void addSessions(ArrayList<Session> sessions) {
        sessions.addAll(sessions);
    }
}
