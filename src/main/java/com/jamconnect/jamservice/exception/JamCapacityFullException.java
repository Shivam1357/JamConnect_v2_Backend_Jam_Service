package com.jamconnect.jamservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class JamCapacityFullException extends BusinessRuleException {

    public JamCapacityFullException(UUID jamId) {
        super("Jam " + jamId + " has reached its maximum capacity", HttpStatus.CONFLICT);
    }
}