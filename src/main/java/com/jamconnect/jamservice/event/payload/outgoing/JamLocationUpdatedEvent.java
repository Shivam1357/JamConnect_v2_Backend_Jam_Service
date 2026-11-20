package com.jamconnect.jamservice.event.payload.outgoing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamLocationUpdatedEvent implements Serializable {

    private String eventId;
    private LocalDateTime occurredAt;

    private UUID jamId;
    private JamAddressSnapshot address;

    private LocalDateTime updatedAt;
}
