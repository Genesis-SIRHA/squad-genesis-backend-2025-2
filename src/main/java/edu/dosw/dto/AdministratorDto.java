package edu.dosw.dto;

/**
 * Data Transfer Object for administrator information
 *
 * @param fullName The full name of the administrator
 * @param identityDocument The identity document number of the administrator
 */
public record AdministratorDto(String fullName, String identityDocument) {}
