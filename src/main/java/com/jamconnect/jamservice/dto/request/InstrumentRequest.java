package com.jamconnect.jamservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentRequest {

    @NotNull
    private UUID instrumentId;

    @NotBlank
    private String instrumentName;

    private String iconKey;

    private String iconUrl;

    @Min(1)
    @Builder.Default
    private Integer slotsNeeded = 1;
}