package edu.dosw.dto;

import java.time.LocalDate;

public record UpdateRequestPeriodDTO(
    LocalDate initialDate, LocalDate finalDate, boolean isActive) {}
