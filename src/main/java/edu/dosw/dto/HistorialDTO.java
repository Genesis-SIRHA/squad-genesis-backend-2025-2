package edu.dosw.dto;

import edu.dosw.model.enums.HistorialStatus;

/**
 * Data Transfer Object for historial records
 *
 * @param studentId The unique identifier of the student
 * @param groupCode The unique code identifying the group
 * @param status The status of the historial record
 */
public record HistorialDTO(String studentId, String groupCode, HistorialStatus status) {}
