package edu.dosw.dto;

/**
 * Data Transfer Object for updating course information
 *
 * @param courseName The updated name of the course
 * @param credits The updated number of credits for the course
 */
public record UpdateCourseDTO(String courseName, Integer credits) {}
