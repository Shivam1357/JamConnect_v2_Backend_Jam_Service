package com.jamconnect.jamservice.event.payload.incoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mirrors User Service's UserRegisteredEvent, but only the fields
 * Jam Service actually needs for its local user_reference table.
 * Extra fields on the source JSON (bio, roles, genres, etc.) are
 * simply ignored during deserialization.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent implements Serializable {

    private String eventId;
    private LocalDateTime occurredAt;

    private UUID userId;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
}
