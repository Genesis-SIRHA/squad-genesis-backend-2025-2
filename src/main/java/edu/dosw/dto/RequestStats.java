package edu.dosw.dto;

/**
 * Data Transfer Object that represents statistics about requests. Contains counts for different
 * request statuses.
 *
 * @param total Total number of requests
 * @param pending Number of requests with PENDING status
 * @param approved Number of requests with ACCEPTED status
 * @param rejected Number of requests with REJECTED status
 */
public record RequestStats(Integer total, Integer pending, Integer approved, Integer rejected) {}
