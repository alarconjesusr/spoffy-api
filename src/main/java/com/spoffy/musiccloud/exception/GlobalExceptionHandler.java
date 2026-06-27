// package com.spoffy.musiccloud.exception;

// import jakarta.validation.ConstraintViolationException;
// import java.time.Instant;
// import java.util.List;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ProblemDetail;
// import org.springframework.validation.FieldError;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.core.AuthenticationException;
// import jakarta.servlet.http.HttpServletRequest;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
//         List<String> details = ex.getBindingResult().getFieldErrors().stream()
//                 .map(GlobalExceptionHandler::formatFieldError)
//                 .toList();
//         return build(HttpStatus.BAD_REQUEST, "Validation failed", details);
//     }

//     @ExceptionHandler(ConstraintViolationException.class)
//     public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
//         List<String> details = ex.getConstraintViolations().stream()
//                 .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
//                 .toList();
//         return build(HttpStatus.BAD_REQUEST, "Validation failed", details);
//     }

//     @ExceptionHandler(ResourceNotFoundException.class)
//     public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
//         return build(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
//     }

//     @ExceptionHandler(DuplicateResourceException.class)
//     public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
//         return build(HttpStatus.CONFLICT, ex.getMessage(), List.of());
//     }

//     @ExceptionHandler(InvalidRangeException.class)
//     public ProblemDetail handleInvalidRange(InvalidRangeException ex) {
//         return build(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, ex.getMessage(), List.of());
//     }

//     @ExceptionHandler(IllegalArgumentException.class)
//     public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
//         return build(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
//     }

//     @ExceptionHandler(AccessDeniedException.class)
//     public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
//         return build(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción", List.of());
//     }

//     @ExceptionHandler(AuthenticationException.class)
//     public ProblemDetail handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
//         if (request.getRequestURI() != null && request.getRequestURI().startsWith("/auth/login")) {
//             return build(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", List.of());
//         }
//         return build(HttpStatus.UNAUTHORIZED, "Debes autenticarte con un JWT válido", List.of());
//     }

//     private ProblemDetail build(HttpStatus status, String message, List<String> details) {
//         ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
//         problemDetail.setTitle(status.getReasonPhrase());
//         problemDetail.setProperty("timestamp", Instant.now());
//         problemDetail.setProperty("details", details);
//         return problemDetail;
//     }

//     private static String formatFieldError(FieldError fieldError) {
//         return fieldError.getField() + ": " + fieldError.getDefaultMessage();
//     }
// }