package edu.dosw.dto;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "requestPeriod")
public record RequestPeriodDTO(
    @Id String id,
    LocalDate initialDate,
    LocalDate finalDate,
    String year,
    String period,
    boolean isActive) {}
