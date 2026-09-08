package com.lost2found.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for invalid request parameters or business logic violations.
 */
public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}
