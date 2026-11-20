package com.jamconnect.jamservice.dto.response;

import com.jamconnect.jamservice.enums.JamStatus;
import com.jamconnect.jamservice.enums.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamResponse {

    private UUID id;
    private UserSummaryResponse organizer;

    private String title;
    private String description;
    private SkillLevel skillLevel;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer maxCapacity;
    private Integer currentParticipantCount;
    private JamStatus status;

    private AddressResponse address;
    private List<GenreResponse> genres;
    private List<InstrumentResponse> instruments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}