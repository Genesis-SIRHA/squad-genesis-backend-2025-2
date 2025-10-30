package edu.dosw.dto;

/**
 * Data Transfer Object for dean information
 *
 * @param fullName The full name of the dean
 * @param identityDocument The identity document number of the dean
 * @param facultyName The name of the faculty the dean is assigned to
 */
public record DeanDto(String fullName, String identityDocument, String facultyName) {}
