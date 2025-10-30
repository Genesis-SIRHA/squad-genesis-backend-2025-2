package edu.dosw.exception;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }

  /**
   * Constructs a BusinessException with the specified detail message and cause
   *
   * @param message The detail message explaining the business rule violation
   * @param cause The underlying cause of the exception
   */
  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
