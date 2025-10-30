package edu.dosw.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating request period information
 *
 * @param initialDate The updated start date of the request period
 * @param finalDate The updated end date of the request period
 * @param isActive Indicates whether this request period should be active
 */
public record UpdateRequestPeriodDTO(
    LocalDate initialDate, LocalDate finalDate, boolean isActive) {}
