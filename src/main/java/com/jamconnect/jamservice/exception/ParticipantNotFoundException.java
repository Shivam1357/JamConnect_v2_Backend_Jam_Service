package com.jamconnect.jamservice.exception;

import java.util.UUID;

public class ParticipantNotFoundException extends ResourceNotFoundException {

    public ParticipantNotFoundException(UUID jamId, UUID userId) {
        super("User " + userId + " is not a participant of jam " + jamId);
    }
}