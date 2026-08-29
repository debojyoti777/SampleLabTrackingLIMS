package com.labtrack.sampletracking.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception handling for every {@code @RestController} in the
 * application. Replaces per-controller {@code @ExceptionHandler} methods so
 * every endpoint returns a consistent {@link ErrorResponse} body instead of
 * each controller deciding its own error format (or falling through to
 * Spring Boot's generic, detail-free 500 response).
 *
 * @author Debojyoti Mallick
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * A requested Sample doesn't exist -> 404.
     */
    @ExceptionHandler(SampleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSampleNotFound(SampleNotFoundException ex) {
        ErrorResponse body =  new ErrorResponse( ErrorCodes.NOT_FOUND, ex.getErrorMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Bad input that isn't a validation-annotation failure - e.g. an invalid
     * search column, or a status transition that isn't logically allowed
     * (see {@link IllegalUpdateException }) -> 403.
     */

    @ExceptionHandler(IllegalUpdateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalUpdate(IllegalUpdateException ex)
    {
        ErrorResponse body = new ErrorResponse(ErrorCodes.ILLEGAL_UPDATE , ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Bean Validation failures from {@code @Valid} on request DTOs (e.g. a
     * missing {@code @NotBlank} field on {@code SampleRequest}) -> 400, with
     * every failing field's message collected instead of just the first one.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex)
    {
        String message = String.valueOf(ex.getBindingResult().getFieldError());
        ErrorResponse body = new ErrorResponse(ErrorCodes.LOGICAL_ERROR , message, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Fallback for anything not explicitly handled above -> 500. Deliberately
     * generic to the client (no stack trace/internal details leaked), but
     * still surfaced as a structured error.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGenericLogicalError(RuntimeException ex)
    {
        ErrorResponse body = new ErrorResponse( ErrorCodes.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again",
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * A requested Batch doesn't exist -> 404.
     */
    @ExceptionHandler(BatchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBatchNotFoundError(BatchNotFoundException ex)
    {
        ErrorResponse body = new ErrorResponse(ErrorCodes.NOT_FOUND, ex.getErrorMessage(), 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
