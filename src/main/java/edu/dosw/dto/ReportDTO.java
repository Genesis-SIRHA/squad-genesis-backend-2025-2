package edu.dosw.dto;

/** DTO for reporting reassignment statistics */
public record ReportDTO(
     Integer total,
    Integer pending,
    Integer approved,
    Integer rejected,
    Integer cancellations,
    Integer swaps,
     Integer joins) {}
