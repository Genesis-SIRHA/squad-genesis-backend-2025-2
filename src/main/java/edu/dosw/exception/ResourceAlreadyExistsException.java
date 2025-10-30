package edu.dosw.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
  /**
   * Constructs a ResourceAlreadyExistsException with the specified detail message
   *
   * @param message The detail message explaining the resource conflict
   */
  public ResourceAlreadyExistsException(String message) {
    super(message);
  }
}
