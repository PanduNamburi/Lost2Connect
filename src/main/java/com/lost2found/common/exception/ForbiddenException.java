package com.lost2found.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authenticated user does not have permission for the resource.
 */
public class ForbiddenException extends AppException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
