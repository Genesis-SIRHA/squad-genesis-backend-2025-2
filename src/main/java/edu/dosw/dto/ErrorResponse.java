package edu.dosw.dto;

import java.time.LocalDateTime;

/** Represents an error response containing details about API errors */
public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String code;
  private String message;
  private String path;

  /**
   * Constructs an ErrorResponse with the specified details
   *
   * @param status The HTTP status code
   * @param error The error type description
   * @param code The application-specific error code
   * @param message The detailed error message
   * @param path The API path where the error occurred
   */
  public ErrorResponse(int status, String error, String code, String message, String path) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.code = code;
    this.message = message;
    this.path = path;
  }

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
