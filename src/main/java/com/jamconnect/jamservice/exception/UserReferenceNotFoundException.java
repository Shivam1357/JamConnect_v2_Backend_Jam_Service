package com.jamconnect.jamservice.exception;

import java.util.UUID;

public class UserReferenceNotFoundException extends ResourceNotFoundException {

    public UserReferenceNotFoundException(UUID userId) {
        super("No user reference found for id: " + userId
                + " (not yet synced via Kafka and fallback lookup failed)");
    }
}