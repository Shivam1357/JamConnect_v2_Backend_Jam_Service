package com.jamconnect.jamservice.event.payload.outgoing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamGenreSnapshot implements Serializable {

    private UUID genreId;
    private String genreName;
}
