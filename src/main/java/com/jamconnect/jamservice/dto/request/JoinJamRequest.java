package com.jamconnect.jamservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * jamId and userId come from the path/JWT respectively — this body
 * only carries optional extra context about how the user is joining.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinJamRequest {

    private UUID instrumentId;
}