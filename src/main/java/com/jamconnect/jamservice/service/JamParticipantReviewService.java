package com.jamconnect.jamservice.service;

import com.jamconnect.jamservice.dto.request.CreateParticipantReviewRequest;
import com.jamconnect.jamservice.dto.response.ParticipantReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JamParticipantReviewService {

    ParticipantReviewResponse submitReview(UUID jamId, UUID reviewerId, CreateParticipantReviewRequest request);

    Page<ParticipantReviewResponse> getReviewsForUser(UUID userId, Pageable pageable);

    Page<ParticipantReviewResponse> getReviewsForJam(UUID jamId, Pageable pageable);

    Double getAverageRatingForUser(UUID userId);
}
