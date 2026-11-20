package com.jamconnect.jamservice.dto.request;

import com.jamconnect.jamservice.enums.SkillLevel;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Partial update — all fields optional, only non-null fields are applied.
 * Location changes go through a separate UpdateJamLocationRequest/endpoint,
 * mirroring the JamUpdatedEvent / JamLocationUpdatedEvent split.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateJamRequest {

    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private SkillLevel skillLevel;

    @Future(message = "startTime must be in the future")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(1)
    private Integer maxCapacity;
}