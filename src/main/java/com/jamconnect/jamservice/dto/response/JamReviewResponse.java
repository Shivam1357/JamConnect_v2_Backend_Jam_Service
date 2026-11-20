package com.jamconnect.jamservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamReviewResponse {

    private UUID id;
    private UserSummaryResponse reviewer;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}