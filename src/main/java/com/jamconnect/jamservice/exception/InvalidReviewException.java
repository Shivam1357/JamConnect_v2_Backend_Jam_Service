package com.jamconnect.jamservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidReviewException extends BusinessRuleException {

    public InvalidReviewException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}