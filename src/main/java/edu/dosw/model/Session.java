package model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sessions")
public class Session {
    private String id;
    private String roomName;
    private int timeSlot;
    private DayOfWeek day;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        if (timeSlot < 1 || timeSlot > 7) {
            throw new IllegalArgumentException("Time slot must be between 1 and 7");
        }
        this.timeSlot = timeSlot;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }
}
