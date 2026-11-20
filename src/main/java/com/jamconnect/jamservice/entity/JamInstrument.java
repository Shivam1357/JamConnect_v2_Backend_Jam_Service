package com.jamconnect.jamservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "jam_instruments", indexes = {
        @Index(name = "idx_jam_instrument_jam_id", columnList = "jam_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamInstrument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jam_id", nullable = false)
    private Jam jam;

    @Column(name = "instrument_id", nullable = false)
    private UUID instrumentId;

    @Column(name = "instrument_name", nullable = false)
    private String instrumentName;

    @Column(name = "icon_key")
    private String iconKey;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "slots_needed", nullable = false)
    @Builder.Default
    private Integer slotsNeeded = 1;
}