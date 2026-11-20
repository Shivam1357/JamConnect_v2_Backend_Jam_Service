package com.jamconnect.jamservice.service;

import com.jamconnect.jamservice.dto.request.CreateJamRequest;
import com.jamconnect.jamservice.dto.request.UpdateJamLocationRequest;
import com.jamconnect.jamservice.dto.request.UpdateJamRequest;
import com.jamconnect.jamservice.dto.response.JamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JamService {

    JamResponse createJam(UUID organizerId, CreateJamRequest request);

    JamResponse getJamById(UUID jamId);

    Page<JamResponse> listUpcomingJams(Pageable pageable);

    Page<JamResponse> listJamsByOrganizer(UUID organizerId, Pageable pageable);

    JamResponse updateJam(UUID jamId, UUID requesterId, UpdateJamRequest request);

    JamResponse updateJamLocation(UUID jamId, UUID requesterId, UpdateJamLocationRequest request);

    void cancelJam(UUID jamId, UUID requesterId, String reason);
}
