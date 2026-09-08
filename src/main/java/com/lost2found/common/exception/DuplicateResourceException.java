package com.lost2found.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when creating or updating a resource conflicts with existing unique data.
 */
public class DuplicateResourceException extends AppException {

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue), HttpStatus.CONFLICT);
    }

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
