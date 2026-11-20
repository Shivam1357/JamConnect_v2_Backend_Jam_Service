package com.jamconnect.jamservice.controller;

import com.jamconnect.jamservice.dto.request.CreateJamReviewRequest;
import com.jamconnect.jamservice.dto.response.ApiResponse;
import com.jamconnect.jamservice.dto.response.JamReviewResponse;
import com.jamconnect.jamservice.service.JamReviewService;
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

@RestController
@RequestMapping("/api/jams/{jamId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Jam Reviews", description = "Reviews of the jam session itself (organization, vibe, venue)")
public class JamReviewController {

    private final JamReviewService jamReviewService;

    @PostMapping
    @Operation(summary = "Review a completed jam session")
    public ResponseEntity<ApiResponse<JamReviewResponse>> submitReview(
            @AuthenticationPrincipal UUID reviewerId,
            @PathVariable UUID jamId,
            @Valid @RequestBody CreateJamReviewRequest request) {
        JamReviewResponse response = jamReviewService.submitReview(jamId, reviewerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    @GetMapping
    @Operation(summary = "List reviews for a jam (public)")
    public ResponseEntity<ApiResponse<Page<JamReviewResponse>>> getReviewsForJam(
            @PathVariable UUID jamId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<JamReviewResponse> response = jamReviewService.getReviewsForJam(jamId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/average")
    @Operation(summary = "Get the average rating for a jam (public)")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable UUID jamId) {
        Double average = jamReviewService.getAverageRating(jamId);
        return ResponseEntity.ok(ApiResponse.success(average));
    }
}