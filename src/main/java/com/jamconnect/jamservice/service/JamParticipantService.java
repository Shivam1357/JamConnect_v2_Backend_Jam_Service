package com.jamconnect.jamservice.service;

import com.jamconnect.jamservice.dto.response.ParticipantResponse;
import com.jamconnect.jamservice.enums.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JamParticipantService {

    ParticipantResponse joinJam(UUID jamId, UUID userId);

    void leaveJam(UUID jamId, UUID userId);

    Page<ParticipantResponse> listParticipants(UUID jamId, ParticipantStatus status, Pageable pageable);

    Page<ParticipantResponse> listJamsJoinedByUser(UUID userId, Pageable pageable);
}
