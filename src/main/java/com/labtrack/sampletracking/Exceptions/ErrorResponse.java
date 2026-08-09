package com.labtrack.sampletracking.Exceptions;

import java.time.LocalDateTime;

/**
 * Structured error body returned by {@link GlobalExceptionHandler} for every
 * handled exception, so API consumers get a consistent shape regardless of
 * which error occurred.
 *
 */

public class ErrorResponse {

    final String message;
    final LocalDateTime time;
    final String status;
    final int errorCode;

    public ErrorResponse( String status, String message, int errorCode ) {
        this.message = message;
        this.time = LocalDateTime.now();
        this.status = status;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
