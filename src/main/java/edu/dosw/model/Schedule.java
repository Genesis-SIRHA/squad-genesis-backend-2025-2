package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private String studentId;
    private ArrayList<Session> sessions;

    public void deleteGroup(String abbreviation, String groupId) {
        sessions.removeIf(session -> session.getAbbreviation().equals(abbreviation) && session.getGroupCode().equals(groupId));
    }

    public void addSessions(ArrayList<Session> sessions) {
        sessions.addAll(sessions);
    }
}
