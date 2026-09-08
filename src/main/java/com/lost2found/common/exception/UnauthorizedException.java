package com.lost2found.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails or credentials are missing/invalid.
 */
public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
