package edu.dosw.dto;

/**
 * DTO for reporting reassignment statistics
 */
public record ReportDTO(
        long total,
        long pending,
        long approved,
        long rejected,
        long cancellations,
        long swaps,
        long joins
) {}