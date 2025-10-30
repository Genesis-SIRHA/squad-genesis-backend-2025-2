package edu.dosw.exception;

import edu.dosw.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles business rule violations
   *
   * @param ex The BusinessException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException ex, HttpServletRequest request) {
    logger.error("Business error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex.getMessage(), request.getRequestURI());
  }

  /**
   * Handles requests for resources that cannot be found
   *
   * @param ex The ResourceNotFoundException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    logger.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse(
        HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request.getRequestURI());
  }

  /**
   * Handles attempts to create resources that already exist
   *
   * @param ex The ResourceAlreadyExistsException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(
      ResourceAlreadyExistsException ex, HttpServletRequest request) {
    logger.error("Conflict at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS", ex.getMessage(), request.getRequestURI());
  }

  /**
   * Handles general validation exceptions
   *
   * @param ex The ValidationException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      ValidationException ex, HttpServletRequest request) {
    logger.debug("Validation error at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse(
        HttpStatus.UNPROCESSABLE_ENTITY,
        "VALIDATION_ERROR",
        ex.getMessage(),
        request.getRequestURI());
  }

  /**
   * Handles method argument validation failures
   *
   * @param ex The MethodArgumentNotValidException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    logger.debug("Invalid arguments at {}: {}", request.getRequestURI(), errors);
    return buildResponse(
        HttpStatus.BAD_REQUEST, "INVALID_ARGUMENTS", errors, request.getRequestURI());
  }

  /**
   * Handles constraint violation exceptions
   *
   * @param ex The ConstraintViolationException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    String errors =
        ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining(", "));
    logger.debug("Constraint violation at {}: {}", request.getRequestURI(), errors);
    return buildResponse(
        HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", errors, request.getRequestURI());
  }

  /**
   * Handles malformed JSON requests
   *
   * @param ex The HttpMessageNotReadableException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    logger.error("Malformed JSON at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "MALFORMED_JSON",
        "JSON mal formado: " + ex.getMostSpecificCause().getMessage(),
        request.getRequestURI());
  }

  /**
   * Handles authentication failures
   *
   * @param ex The AuthenticationException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthentication(
      AuthenticationException ex, HttpServletRequest request) {
    logger.warn("Authentication failed at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse(
        HttpStatus.UNAUTHORIZED,
        "AUTH_FAILED",
        "Error de autenticación: " + ex.getMessage(),
        request.getRequestURI());
  }

  /**
   * Handles access denied scenarios
   *
   * @param ex The AccessDeniedException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    logger.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse(
        HttpStatus.FORBIDDEN,
        "ACCESS_DENIED",
        "Acceso denegado: " + ex.getMessage(),
        request.getRequestURI());
  }

  /**
   * Handles data integrity violations (e.g., duplicate keys, foreign key constraints)
   *
   * @param ex The DataIntegrityViolationException that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    logger.error(
        "Data integrity violation at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.CONFLICT,
        "DATA_INTEGRITY_ERROR",
        "Error de integridad de datos: " + ex.getMostSpecificCause().getMessage(),
        request.getRequestURI());
  }

  /**
   * Handles all other uncaught exceptions
   *
   * @param ex The Exception that was thrown
   * @param request The HTTP request that caused the exception
   * @return ResponseEntity with error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
    logger.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "UNEXPECTED_ERROR",
        "Error inesperado: " + ex.getMessage(),
        request.getRequestURI());
  }

  /**
   * Builds a standardized error response
   *
   * @param status The HTTP status code
   * @param code The application-specific error code
   * @param message The error message
   * @param path The request path where the error occurred
   * @return ResponseEntity containing the ErrorResponse
   */
  private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status, String code, String message, String path) {
    ErrorResponse errorResponse =
        new ErrorResponse(status.value(), status.getReasonPhrase(), code, message, path);
    return new ResponseEntity<>(errorResponse, status);
  }
}
