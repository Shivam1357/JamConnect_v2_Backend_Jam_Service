package com.jamconnect.jamservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Frontend already fetched the genre list from User Service, so it
 * sends id + display fields directly. Jam Service trusts this and
 * snapshots it — no cross-service validation call.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreRequest {

    @NotNull
    private UUID genreId;

    @NotBlank
    private String genreName;

    private String iconKey;

    private String iconUrl;
}