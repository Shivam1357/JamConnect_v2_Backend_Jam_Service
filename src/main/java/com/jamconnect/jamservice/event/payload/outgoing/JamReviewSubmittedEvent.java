package com.jamconnect.jamservice.event.payload.outgoing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/** Session-level review. See JamParticipantReviewSubmittedEvent for peer reviews. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamReviewSubmittedEvent implements Serializable {

    private String eventId;
    private LocalDateTime occurredAt;

    private UUID jamId;
    private UUID reviewerId;
    private Integer rating;
    private String comment;

    private LocalDateTime createdAt;
}
