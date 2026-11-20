package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.JamAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JamAddressRepository extends JpaRepository<JamAddress, UUID> {

    Optional<JamAddress> findByJamId(UUID jamId);

    List<JamAddress> findByCity(String city);

    /**
     * Simple bounding-box geo lookup for "jams near me" style queries.
     * Fine for now at small scale — swap for OpenSearch geo-distance
     * queries via AI/Search Service once that's wired up.
     */
    @Query("""
            SELECT a FROM JamAddress a
            WHERE a.latitude BETWEEN :minLat AND :maxLat
            AND a.longitude BETWEEN :minLng AND :maxLng
            """)
    List<JamAddress> findWithinBoundingBox(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng
    );
}