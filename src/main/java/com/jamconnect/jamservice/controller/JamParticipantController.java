package com.jamconnect.jamservice.controller;

import com.jamconnect.jamservice.dto.request.JoinJamRequest;
import com.jamconnect.jamservice.dto.response.ApiResponse;
import com.jamconnect.jamservice.dto.response.ParticipantResponse;
import com.jamconnect.jamservice.enums.ParticipantStatus;
import com.jamconnect.jamservice.service.JamParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequiredArgsConstructor
@Tag(name = "Jam Participants", description = "Join/leave a jam, and browse who's attending")
public class JamParticipantController {

    private final JamParticipantService jamParticipantService;

    /**
     * NOTE: JoinJamRequest.instrumentId is accepted here for forward
     * compatibility, but JamParticipantService doesn't persist it yet —
     * JamParticipant has no instrument column. Wire this through later
     * if "which instrument I'm bringing" needs to be tracked per-participant.
     */
    @PostMapping("/api/jams/{jamId}/participants")
    @Operation(summary = "Join a jam (auto-waitlisted if full)")
    public ResponseEntity<ApiResponse<ParticipantResponse>> joinJam(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID jamId,
            @RequestBody(required = false) JoinJamRequest request) {
        ParticipantResponse response = jamParticipantService.joinJam(jamId, userId);
        String message = response.getStatus() == ParticipantStatus.WAITLISTED
                ? "Jam is full — you've been added to the waitlist"
                : "Joined jam successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(message, response));
    }

    @DeleteMapping("/api/jams/{jamId}/participants/me")
    @Operation(summary = "Leave a jam")
    public ResponseEntity<ApiResponse<Void>> leaveJam(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID jamId) {
        jamParticipantService.leaveJam(jamId, userId);
        return ResponseEntity.ok(ApiResponse.success("Left jam successfully"));
    }

    @GetMapping("/api/jams/{jamId}/participants")
    @Operation(summary = "List participants of a jam by status (public browsing)")
    public ResponseEntity<ApiResponse<Page<ParticipantResponse>>> listParticipants(
            @PathVariable UUID jamId,
            @RequestParam(defaultValue = "JOINED") ParticipantStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ParticipantResponse> response = jamParticipantService.listParticipants(jamId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Deliberately NOT under /api/jams/** — that prefix is public for GET
     * requests (see SecurityConfig), and "jams I've joined" must stay
     * authenticated since it depends on the caller's own identity.
     */
    @GetMapping("/api/me/participations")
    @Operation(summary = "List jams the current user has joined")
    public ResponseEntity<ApiResponse<Page<ParticipantResponse>>> listMyParticipations(
            @AuthenticationPrincipal UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ParticipantResponse> response = jamParticipantService.listJamsJoinedByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}