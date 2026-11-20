package com.jamconnect.jamservice.controller;

import com.jamconnect.jamservice.dto.request.CreateJamRequest;
import com.jamconnect.jamservice.dto.request.UpdateJamLocationRequest;
import com.jamconnect.jamservice.dto.request.UpdateJamRequest;
import com.jamconnect.jamservice.dto.response.ApiResponse;
import com.jamconnect.jamservice.dto.response.JamResponse;
import com.jamconnect.jamservice.service.JamService;
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
 * Read endpoints (GET /api/jams/**) are public browsing/discovery —
 * see SecurityConfig's PUBLIC_GET_ENDPOINTS. Everything else here
 * (create/update/cancel) requires authentication; ownership checks
 * (only the organizer can edit/cancel) are enforced in JamService.
 */
@RestController
@RequestMapping("/api/jams")
@RequiredArgsConstructor
@Tag(name = "Jams", description = "Jam session creation, browsing, and management")
public class JamController {

    private final JamService jamService;

    @PostMapping
    @Operation(summary = "Create a new jam session")
    public ResponseEntity<ApiResponse<JamResponse>> createJam(
            @AuthenticationPrincipal UUID organizerId,
            @Valid @RequestBody CreateJamRequest request) {
        JamResponse response = jamService.createJam(organizerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Jam created successfully", response));
    }

    @GetMapping("/{jamId}")
    @Operation(summary = "Get a jam by id")
    public ResponseEntity<ApiResponse<JamResponse>> getJamById(@PathVariable UUID jamId) {
        JamResponse response = jamService.getJamById(jamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List upcoming jams")
    public ResponseEntity<ApiResponse<Page<JamResponse>>> listUpcomingJams(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<JamResponse> response = jamService.listUpcomingJams(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/organizer/{organizerId}")
    @Operation(summary = "List jams created by a specific organizer")
    public ResponseEntity<ApiResponse<Page<JamResponse>>> listJamsByOrganizer(
            @PathVariable UUID organizerId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<JamResponse> response = jamService.listJamsByOrganizer(organizerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{jamId}")
    @Operation(summary = "Update a jam's core details (organizer only)")
    public ResponseEntity<ApiResponse<JamResponse>> updateJam(
            @AuthenticationPrincipal UUID requesterId,
            @PathVariable UUID jamId,
            @Valid @RequestBody UpdateJamRequest request) {
        JamResponse response = jamService.updateJam(jamId, requesterId, request);
        return ResponseEntity.ok(ApiResponse.success("Jam updated successfully", response));
    }

    @PutMapping("/{jamId}/location")
    @Operation(summary = "Update a jam's location (organizer only)")
    public ResponseEntity<ApiResponse<JamResponse>> updateJamLocation(
            @AuthenticationPrincipal UUID requesterId,
            @PathVariable UUID jamId,
            @Valid @RequestBody UpdateJamLocationRequest request) {
        JamResponse response = jamService.updateJamLocation(jamId, requesterId, request);
        return ResponseEntity.ok(ApiResponse.success("Jam location updated successfully", response));
    }

    @DeleteMapping("/{jamId}")
    @Operation(summary = "Cancel a jam (organizer only)")
    public ResponseEntity<ApiResponse<Void>> cancelJam(
            @AuthenticationPrincipal UUID requesterId,
            @PathVariable UUID jamId,
            @RequestParam(required = false, defaultValue = "Cancelled by organizer") String reason) {
        jamService.cancelJam(jamId, requesterId, reason);
        return ResponseEntity.ok(ApiResponse.success("Jam cancelled successfully"));
    }
}