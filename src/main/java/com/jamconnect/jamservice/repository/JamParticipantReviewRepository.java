package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.JamParticipantReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JamParticipantReviewRepository extends JpaRepository<JamParticipantReview, UUID> {

    Page<JamParticipantReview> findByJamId(UUID jamId, Pageable pageable);

    Page<JamParticipantReview> findByReviewedUserId(UUID reviewedUserId, Pageable pageable);

    Optional<JamParticipantReview> findByJamIdAndReviewerIdAndReviewedUserId(
            UUID jamId, UUID reviewerId, UUID reviewedUserId);

    boolean existsByJamIdAndReviewerIdAndReviewedUserId(
            UUID jamId, UUID reviewerId, UUID reviewedUserId);

    @Query("SELECT AVG(r.rating) FROM JamParticipantReview r WHERE r.reviewedUser.id = :userId")
    Double findAverageRatingByReviewedUserId(@Param("userId") UUID userId);
}