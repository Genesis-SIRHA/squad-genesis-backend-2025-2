package edu.dosw.dto;

import java.time.LocalDate;

public record CreateRequestPeriodDTO(
    LocalDate initialDate, LocalDate finalDate, String year, String period) {}
