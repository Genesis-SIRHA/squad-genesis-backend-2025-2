package edu.dosw.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "historial")
public class Historial {
  private String studentId;
  private String groupCode;
  private String status;
  private String year;
  private String period;
}
