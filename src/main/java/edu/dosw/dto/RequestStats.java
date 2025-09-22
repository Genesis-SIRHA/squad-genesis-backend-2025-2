package edu.dosw.dto;

public record RequestStats(
        long total,
        long pending,
        long approved,
        long rejected
) {}