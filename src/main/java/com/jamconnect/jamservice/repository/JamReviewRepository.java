package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.JamReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JamReviewRepository extends JpaRepository<JamReview, UUID> {

    Page<JamReview> findByJamId(UUID jamId, Pageable pageable);

    Optional<JamReview> findByJamIdAndReviewerId(UUID jamId, UUID reviewerId);

    boolean existsByJamIdAndReviewerId(UUID jamId, UUID reviewerId);

    @Query("SELECT AVG(r.rating) FROM JamReview r WHERE r.jam.id = :jamId")
    Double findAverageRatingByJamId(@Param("jamId") UUID jamId);
}