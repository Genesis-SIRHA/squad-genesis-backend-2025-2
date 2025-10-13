package edu.dosw.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String code;
  private String message;
  private String path;

  public ErrorResponse(int status, String error, String code, String message, String path) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.code = code;
    this.message = message;
    this.path = path;
  }

  // Getters y setters
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }
}
