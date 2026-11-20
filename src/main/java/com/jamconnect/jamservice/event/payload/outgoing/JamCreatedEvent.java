package com.jamconnect.jamservice.event.payload.outgoing;

import com.jamconnect.jamservice.enums.JamStatus;
import com.jamconnect.jamservice.enums.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamCreatedEvent implements Serializable {

    private String eventId;
    private LocalDateTime occurredAt;

    private UUID jamId;
    private UUID organizerId;

    private String title;
    private String description;
    private SkillLevel skillLevel;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer maxCapacity;
    private JamStatus status;

    private JamAddressSnapshot address;
    private List<JamGenreSnapshot> genres;
    private List<JamInstrumentSnapshot> instruments;

    private LocalDateTime createdAt;
}
