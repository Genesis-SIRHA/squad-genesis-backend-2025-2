package edu.dosw.dto;

import edu.dosw.model.enums.HistorialStatus;

public record HistorialDTO(String studentId, String groupCode, HistorialStatus status) {}
