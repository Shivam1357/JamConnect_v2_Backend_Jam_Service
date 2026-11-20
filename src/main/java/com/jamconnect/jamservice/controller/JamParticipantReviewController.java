package com.jamconnect.jamservice.controller;

import com.jamconnect.jamservice.dto.request.CreateParticipantReviewRequest;
import com.jamconnect.jamservice.dto.response.ApiResponse;
import com.jamconnect.jamservice.dto.response.ParticipantReviewResponse;
import com.jamconnect.jamservice.service.JamParticipantReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * All read routes here are kept under the /api/jams/** prefix on purpose —
 * that's the existing public-GET pattern in SecurityConfig, so these
 * "browse a musician's reputation" style endpoints stay public without
 * needing a separate security rule.
 */
@RestController
@RequestMapping("/api/jams")
@RequiredArgsConstructor
@Tag(name = "Participant Reviews", description = "Peer-to-peer reviews between jam participants")
public class JamParticipantReviewController {

    private final JamParticipantReviewService jamParticipantReviewService;

    @PostMapping("/{jamId}/participant-reviews")
    @Operation(summary = "Review a fellow participant after a completed jam")
    public ResponseEntity<ApiResponse<ParticipantReviewResponse>> submitReview(
            @AuthenticationPrincipal UUID reviewerId,
            @PathVariable UUID jamId,
            @Valid @RequestBody CreateParticipantReviewRequest request) {
        ParticipantReviewResponse response = jamParticipantReviewService.submitReview(jamId, reviewerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    @GetMapping("/{jamId}/participant-reviews")
    @Operation(summary = "List peer reviews submitted for a specific jam (public)")
    public ResponseEntity<ApiResponse<Page<ParticipantReviewResponse>>> getReviewsForJam(
            @PathVariable UUID jamId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ParticipantReviewResponse> response = jamParticipantReviewService.getReviewsForJam(jamId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/participant-reviews/users/{userId}")
    @Operation(summary = "List reviews a musician has received across all jams (public reputation view)")
    public ResponseEntity<ApiResponse<Page<ParticipantReviewResponse>>> getReviewsForUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ParticipantReviewResponse> response = jamParticipantReviewService.getReviewsForUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/participant-reviews/users/{userId}/average")
    @Operation(summary = "Get a musician's average peer rating (public)")
    public ResponseEntity<ApiResponse<Double>> getAverageRatingForUser(@PathVariable UUID userId) {
        Double average = jamParticipantReviewService.getAverageRatingForUser(userId);
        return ResponseEntity.ok(ApiResponse.success(average));
    }
}