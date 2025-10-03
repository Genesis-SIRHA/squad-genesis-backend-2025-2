package edu.dosw.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class Schedule {
  private String studentId;
  private ArrayList<Session> sessions;

  public Schedule(String studentId, ArrayList<Session> sessions) {
    this.studentId = studentId;
    this.sessions = sessions;
  }
}
