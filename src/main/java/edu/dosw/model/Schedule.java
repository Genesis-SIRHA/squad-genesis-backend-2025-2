package edu.dosw.model;

import java.util.ArrayList;
import lombok.Data;

/** Represents a student's academic schedule containing all their sessions */
@Data
public class Schedule {
  private String studentId;
  private ArrayList<Session> sessions;

  /**
   * Constructs a Schedule for a student with their sessions
   *
   * @param studentId The unique identifier of the student
   * @param sessions The list of sessions in the student's schedule
   */
  public Schedule(String studentId, ArrayList<Session> sessions) {
    this.studentId = studentId;
    this.sessions = sessions;
  }
}
