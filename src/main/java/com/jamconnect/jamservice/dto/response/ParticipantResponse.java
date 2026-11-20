package com.jamconnect.jamservice.dto.response;

import com.jamconnect.jamservice.enums.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {

    private UUID id;
    private UserSummaryResponse user;
    private ParticipantStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}