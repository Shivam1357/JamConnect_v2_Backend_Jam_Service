package com.jamconnect.jamservice.exception;

import java.util.UUID;

public class JamNotFoundException extends ResourceNotFoundException {

    public JamNotFoundException(UUID jamId) {
        super("Jam not found with id: " + jamId);
    }
}