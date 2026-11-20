package com.jamconnect.jamservice.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedJamActionException extends BusinessRuleException {

    public UnauthorizedJamActionException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}