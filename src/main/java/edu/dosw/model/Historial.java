package edu.dosw.model;

import edu.dosw.model.enums.HistorialStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "historial")
public class Historial {
    @Id
    private String id;
  private String studentId;
  private String groupCode;
  private HistorialStatus status;
  private String year;
  private String period;

  public Historial(HistorialBuilder builder) {
    this.studentId = builder.studentId;
    this.groupCode = builder.groupCode;
    this.status = builder.status;
    this.year = builder.year;
    this.period = builder.period;
  }

    public static class HistorialBuilder {
    private String studentId;
    private String groupCode;
    private HistorialStatus status;
    private String year;
    private String period;

    public HistorialBuilder studentId(String studentId) {
      this.studentId = studentId;
      return this;
    }

    public HistorialBuilder groupCode(String groupCode) {
      this.groupCode = groupCode;
      return this;
    }

    public HistorialBuilder status(HistorialStatus status) {
      this.status = status;
      return this;
    }

    public HistorialBuilder year(String year) {
      this.year = year;
      return this;
    }

    public HistorialBuilder period(String period) {
      this.period = period;
      return this;
    }

    public Historial build() {
      return new Historial(this);
    }
  }
}
