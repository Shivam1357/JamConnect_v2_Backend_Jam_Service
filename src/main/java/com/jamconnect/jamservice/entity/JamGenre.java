package com.jamconnect.jamservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * A jam can span more than one genre (e.g. "Rock/Blues fusion jam"),
 * so this mirrors the JamInstrument pattern instead of a single
 * genre_id/genre_name column on Jam. genreId/genreName are denormalized
 * from User Service's Genre reference data (trusted from the frontend
 * at creation time — no cross-service validation call).
 */
@Entity
@Table(name = "jam_genres",
        indexes = {
                @Index(name = "idx_jam_genre_jam_id", columnList = "jam_id"),
                @Index(name = "idx_jam_genre_genre_id", columnList = "genre_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_jam_genre", columnNames = {"jam_id", "genre_id"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jam_id", nullable = false)
    private Jam jam;

    @Column(name = "genre_id", nullable = false)
    private UUID genreId;

    @Column(name = "genre_name", nullable = false)
    private String genreName;

    @Column(name = "icon_key")
    private String iconKey;

    @Column(name = "icon_url")
    private String iconUrl;
}