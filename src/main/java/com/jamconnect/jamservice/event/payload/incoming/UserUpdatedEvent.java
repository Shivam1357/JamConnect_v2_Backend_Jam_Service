package com.jamconnect.jamservice.event.payload.incoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent implements Serializable {

    private String eventId;
    private LocalDateTime occurredAt;

    private UUID userId;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    /** Used to skip processing when none of the fields we care about changed. */
    private Set<String> changedFields;
}
