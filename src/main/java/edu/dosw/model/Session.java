package edu.dosw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sessions")
public class Session {
    private String groupCode;
    private String classroomName;
    private int slot;
    private DayOfWeek day;
    private int year;
    private int period;
}
