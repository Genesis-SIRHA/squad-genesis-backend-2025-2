package edu.dosw.exception;

public class ResourceNotFoundException extends RuntimeException {
  /**
   * Constructs a ResourceNotFoundException with the specified detail message
   *
   * @param message The detail message explaining which resource was not found
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
