package com.jamconnect.jamservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Separate table (not embedded in Jam) so latitude/longitude and city
 * can be indexed independently for geo-spatial and city-based search,
 * mirroring the pattern used for Address in User Service.
 */
@Entity
@Table(name = "jam_addresses", indexes = {
        @Index(name = "idx_jam_address_jam_id", columnList = "jam_id"),
        @Index(name = "idx_jam_address_lat_lon", columnList = "latitude, longitude"),
        @Index(name = "idx_jam_address_city", columnList = "city")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jam_id", nullable = false, unique = true)
    private Jam jam;

    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "landmark")
    private String landmark;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "formatted_address", length = 512)
    private String formattedAddress;

    @Column(name = "place_id")
    private String placeId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}