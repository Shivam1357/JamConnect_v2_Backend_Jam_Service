package com.jamconnect.jamservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Minimal user info for inline display (organizer name, participant
 * avatar, etc.) — sourced from the local user_reference table, not
 * a live call to User Service. For full profile data, the frontend
 * calls User Service directly.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
}