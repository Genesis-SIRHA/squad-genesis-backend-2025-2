package edu.dosw.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.DayOfWeek;

@Data
@Document(collection = "sessions")
public class Session {
    private String groupCode;
    private String classroomName;
    private int slot;
    private DayOfWeek day;
    private int year;
    private int period;

    public Session() {
    }

    public Session(String groupCode, String classroomName, int slot, DayOfWeek day, int year, int period) {
        this.groupCode = groupCode;
        this.classroomName = classroomName;
        this.slot = slot;
        this.day = day;
        this.year = year;
        this.period = period;
    }
    
}