package edu.dosw.exception;

/** Exception thrown when a user attempts to access resources without proper authorization */
public class AccessDeniedException extends RuntimeException {
  /**
   * Constructs an AccessDeniedException with the specified detail message
   *
   * @param message The detail message explaining the access denial
   */
  public AccessDeniedException(String message) {
    super(message);
  }
}
