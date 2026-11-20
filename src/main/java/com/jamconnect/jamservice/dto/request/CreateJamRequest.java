package com.jamconnect.jamservice.dto.request;

import com.jamconnect.jamservice.enums.SkillLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJamRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull
    private SkillLevel skillLevel;

    @NotNull
    @Future(message = "startTime must be in the future")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull
    @Min(1)
    private Integer maxCapacity;

    @NotNull
    @Valid
    private AddressRequest address;

    @NotEmpty(message = "At least one genre is required")
    @Valid
    private List<GenreRequest> genres;

    @Valid
    private List<InstrumentRequest> instruments;
}