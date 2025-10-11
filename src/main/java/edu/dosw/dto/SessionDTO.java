package edu.dosw.dto;

import edu.dosw.model.enums.DayOfWeek;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SessionDTO (
      String groupCode,
      String classroomName,
      @Min(value = 1)
      @Max(value = 7)
      Integer slot,
      DayOfWeek day
){}
