package edu.dosw.exception;

/**
 * Exception thrown when authentication fails due to invalid credentials or other authentication
 * issues
 */
public class AuthenticationException extends RuntimeException {
  /**
   * Constructs an AuthenticationException with the specified detail message
   *
   * @param message The detail message explaining the authentication failure
   */
  public AuthenticationException(String message) {
    super(message);
  }
}
