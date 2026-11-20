package com.jamconnect.jamservice.service;

import com.jamconnect.jamservice.dto.request.CreateJamReviewRequest;
import com.jamconnect.jamservice.dto.response.JamReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JamReviewService {

    JamReviewResponse submitReview(UUID jamId, UUID reviewerId, CreateJamReviewRequest request);

    Page<JamReviewResponse> getReviewsForJam(UUID jamId, Pageable pageable);

    Double getAverageRating(UUID jamId);
}
