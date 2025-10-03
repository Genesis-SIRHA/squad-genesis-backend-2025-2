package edu.dosw.dto;

import edu.dosw.model.enums.DayOfWeek;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object representing a session in the academic schedule. Contains information about
 * the room, time slot, and day of the week for a session.
 */
public class SessionDTO {
  /** The name of the room where the session takes place. Cannot be blank. */
  @NotBlank(message = "El nombre del salón es obligatorio")
  private String roomName;

  /** The time slot of the session. Must be a value between 1 and 7 (inclusive). */
  @Min(value = 1, message = "La franja horaria debe ser entre 1 y 7")
  @Max(value = 7, message = "La franja horaria debe ser entre 1 y 7")
  private int timeSlot;

  /** The day of the week when the session occurs. Cannot be null. */
  @NotNull(message = "El día de la semana es obligatorio")
  private DayOfWeek day;

  /**
   * Gets the name of the room where the session takes place.
   *
   * @return the room name
   */
  public String getRoomName() {
    return roomName;
  }

  /**
   * Sets the name of the room where the session takes place.
   *
   * @param roomName the room name to set
   */
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  /**
   * Gets the time slot of the session.
   *
   * @return the time slot (1-7)
   */
  public int getTimeSlot() {
    return timeSlot;
  }

  /**
   * Sets the time slot of the session.
   *
   * @param timeSlot the time slot to set (must be between 1 and 7)
   * @throws IllegalArgumentException if timeSlot is not between 1 and 7
   */
  public void setTimeSlot(int timeSlot) {
    this.timeSlot = timeSlot;
  }

  /**
   * Gets the day of the week when the session occurs.
   *
   * @return the day of the week
   */
  public DayOfWeek getDay() {
    return day;
  }

  /**
   * Sets the day of the week when the session occurs.
   *
   * @param day the day of the week to set
   * @throws IllegalArgumentException if day is null
   */
  public void setDay(DayOfWeek day) {
    this.day = day;
  }
}
