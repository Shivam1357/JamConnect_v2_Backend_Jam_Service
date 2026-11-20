package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.JamParticipant;
import com.jamconnect.jamservice.enums.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JamParticipantRepository extends JpaRepository<JamParticipant, UUID> {

    Optional<JamParticipant> findByJamIdAndUserId(UUID jamId, UUID userId);

    boolean existsByJamIdAndUserIdAndStatus(UUID jamId, UUID userId, ParticipantStatus status);

    Page<JamParticipant> findByJamIdAndStatus(UUID jamId, ParticipantStatus status, Pageable pageable);

    boolean existsByJamIdAndUserId(UUID jamId, UUID userId);

    long countByJamIdAndStatus(UUID jamId, ParticipantStatus status);

    Page<JamParticipant> findByUserId(UUID userId, Pageable pageable);


    /**
     * Oldest waitlisted participant for a jam — used to auto-promote
     * when a JOINED participant leaves and a slot opens up.
     */
    Optional<JamParticipant> findFirstByJamIdAndStatusOrderByJoinedAtAsc(UUID jamId, ParticipantStatus status);

    List<JamParticipant> findByJamId(UUID jamId);
}