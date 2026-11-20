package com.jamconnect.jamservice.dto.response;

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
public class InstrumentResponse {

    private UUID instrumentId;
    private String instrumentName;
    private String iconKey;
    private String iconUrl;
    private Integer slotsNeeded;
}