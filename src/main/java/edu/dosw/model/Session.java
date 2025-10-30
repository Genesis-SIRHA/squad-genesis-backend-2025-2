package edu.dosw.model;

import edu.dosw.model.enums.DayOfWeek;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sessions")
public class Session {
  @Id private String id;
  private String sessionId;
  private String groupCode;
  private String classroomName;
  private int slot;
  private DayOfWeek day;
  private String year;
  private String period;

  /**
   * Constructs a Session using the builder pattern
   *
   * @param builder The SessionBuilder containing session data
   */
  public Session(SessionBuilder builder) {
    this.sessionId = UUID.randomUUID().toString();
    this.groupCode = builder.groupCode;
    this.classroomName = builder.classroomName;
    this.slot = builder.slot;
    this.day = builder.day;
    this.year = builder.year;
    this.period = builder.period;
  }

  /** Builder class for creating Session instances */
  public static class SessionBuilder {
    private String groupCode;
    private String classroomName;
    private int slot;
    private DayOfWeek day;
    private String year;
    private String period;

    public SessionBuilder groupCode(String groupCode) {
      this.groupCode = groupCode;
      return this;
    }

    public SessionBuilder classroomName(String classroomName) {
      this.classroomName = classroomName;
      return this;
    }

    public SessionBuilder slot(int slot) {
      this.slot = slot;
      return this;
    }

    public SessionBuilder day(DayOfWeek day) {
      this.day = day;
      return this;
    }

    public SessionBuilder year(String year) {
      this.year = year;
      return this;
    }

    public SessionBuilder period(String period) {
      this.period = period;
      return this;
    }

    public Session build() {
      return new Session(this);
    }
  }
}
