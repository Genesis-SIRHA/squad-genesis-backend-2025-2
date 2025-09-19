package dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import model.DayOfWeek;

public class SessionDTO {
    @NotBlank(message = "El nombre del salón es obligatorio")
    private String roomName;
    
    @Min(value = 1, message = "La franja horaria debe ser entre 1 y 7")
    @Max(value = 7, message = "La franja horaria debe ser entre 1 y 7")
    private int timeSlot;
    
    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek day;

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
        this.timeSlot = timeSlot;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }
}
