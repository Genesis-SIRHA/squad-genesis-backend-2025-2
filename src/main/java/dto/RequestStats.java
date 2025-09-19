package dto;

public record RequestStats(
        long total,
        long pending,
        long approved,
        long rejected
) {}