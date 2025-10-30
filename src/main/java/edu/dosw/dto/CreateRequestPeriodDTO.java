package edu.dosw.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating a new request period
 *
 * @param initialDate The start date of the request period
 * @param finalDate The end date of the request period
 * @param year The academic year for the request period
 * @param period The academic period (e.g., semester, quarter) for the request period
 */
public record CreateRequestPeriodDTO(
    LocalDate initialDate, LocalDate finalDate, String year, String period) {}
