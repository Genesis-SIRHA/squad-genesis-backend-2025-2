package edu.dosw.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "groups")
public class Group {
  private String groupCode;
  private String abbreviation;
  private String year;
  private String period;
  private String teacherId;
  private boolean isLab;
  private int groupNum;
  private int enrolled;
  private int maxCapacity;
}
