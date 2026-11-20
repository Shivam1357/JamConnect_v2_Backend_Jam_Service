package com.jamconnect.jamservice.event.payload.outgoing;

import com.jamconnect.jamservice.enums.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/** Fires whether the user landed as JOINED or WAITLISTED — status tells which. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamParticipantJoinedEvent implements Serializable {

    private String eventId;
    private LocalDateTime occurredAt;

    private UUID jamId;
    private UUID userId;
    private ParticipantStatus status;
    private Integer currentParticipantCount;

    private LocalDateTime joinedAt;
}
