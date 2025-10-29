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

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException ex, HttpServletRequest request) {
    logger.error("Business error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    logger.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse(
        HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(
      ResourceAlreadyExistsException ex, HttpServletRequest request) {
    logger.error("Conflict at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.CONFLICT, "RESOURCE_ALREADY_EXISTS", ex.getMessage(), request.getRequestURI());
  }

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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
    logger.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "UNEXPECTED_ERROR",
        "Error inesperado: " + ex.getMessage(),
        request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status, String code, String message, String path) {
    ErrorResponse errorResponse =
        new ErrorResponse(status.value(), status.getReasonPhrase(), code, message, path);
    return new ResponseEntity<>(errorResponse, status);
  }
}
