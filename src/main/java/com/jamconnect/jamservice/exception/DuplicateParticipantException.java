package com.jamconnect.jamservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DuplicateParticipantException extends BusinessRuleException {

    public DuplicateParticipantException(UUID jamId, UUID userId) {
        super("User " + userId + " has already joined jam " + jamId, HttpStatus.CONFLICT);
    }
}