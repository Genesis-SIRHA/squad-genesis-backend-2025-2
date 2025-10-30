package edu.dosw.dto;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data Transfer Object for request period information
 *
 * @param id The unique identifier of the request period
 * @param initialDate The start date of the request period
 * @param finalDate The end date of the request period
 * @param year The academic year for the request period
 * @param period The academic period (e.g., semester, quarter) for the request period
 * @param isActive Indicates whether this request period is currently active
 */
@Document(collection = "requestPeriod")
public record RequestPeriodDTO(
    @Id String id,
    LocalDate initialDate,
    LocalDate finalDate,
    String year,
    String period,
    boolean isActive) {}
