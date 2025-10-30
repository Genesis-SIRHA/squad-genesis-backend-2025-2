package edu.dosw.dto;

/**
 * Data Transfer Object for professor information
 *
 * @param fullName The full name of the professor
 * @param identityDocument The identity document number of the professor
 * @param facultyName The name of the faculty the professor is assigned to
 */
public record ProfessorDto(String fullName, String identityDocument, String facultyName) {}
