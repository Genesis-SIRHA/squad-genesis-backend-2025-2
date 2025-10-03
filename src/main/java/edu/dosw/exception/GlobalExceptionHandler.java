package edu.dosw.exception;

import edu.dosw.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
          BusinessException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
          ResourceNotFoundException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(
          ResourceAlreadyExistsException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
          ValidationException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
          MethodArgumentNotValidException ex, HttpServletRequest request) {
    String errors =
            ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
    return buildResponse(HttpStatus.BAD_REQUEST, errors, request.getRequestURI());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
          ConstraintViolationException ex, HttpServletRequest request) {
    String errors =
            ex.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
    return buildResponse(HttpStatus.BAD_REQUEST, errors, request.getRequestURI());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
          HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildResponse(
            HttpStatus.BAD_REQUEST,
            "JSON mal formado: " + ex.getMostSpecificCause().getMessage(),
            request.getRequestURI());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthentication(
          AuthenticationException ex, HttpServletRequest request) {
    return buildResponse(
            HttpStatus.UNAUTHORIZED,
            "Error de autenticación: " + ex.getMessage(),
            request.getRequestURI());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(
          AccessDeniedException ex, HttpServletRequest request) {
    return buildResponse(
            HttpStatus.FORBIDDEN, "Acceso denegado: " + ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
          DataIntegrityViolationException ex, HttpServletRequest request) {
    return buildResponse(
            HttpStatus.CONFLICT,
            "Error de integridad de datos: " + ex.getMostSpecificCause().getMessage(),
            request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
    return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error inesperado: " + ex.getMessage(),
            request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildResponse(
          HttpStatus status, String message, String path) {
    ErrorResponse errorResponse =
            new ErrorResponse(status.value(), status.getReasonPhrase(), message, path);
    return new ResponseEntity<>(errorResponse, status);
  }
}
