package com.jamconnect.jamservice.service.impl;

import com.jamconnect.jamservice.dto.response.ParticipantResponse;
import com.jamconnect.jamservice.entity.Jam;
import com.jamconnect.jamservice.entity.JamParticipant;
import com.jamconnect.jamservice.entity.UserReference;
import com.jamconnect.jamservice.enums.JamStatus;
import com.jamconnect.jamservice.enums.ParticipantStatus;
import com.jamconnect.jamservice.event.payload.outgoing.JamParticipantJoinedEvent;
import com.jamconnect.jamservice.event.payload.outgoing.JamParticipantLeftEvent;
import com.jamconnect.jamservice.event.producer.JamEventProducer;
import com.jamconnect.jamservice.exception.BusinessRuleException;
import com.jamconnect.jamservice.exception.DuplicateParticipantException;
import com.jamconnect.jamservice.exception.JamNotFoundException;
import com.jamconnect.jamservice.exception.ParticipantNotFoundException;
import com.jamconnect.jamservice.mapper.ParticipantMapper;
import com.jamconnect.jamservice.repository.JamParticipantRepository;
import com.jamconnect.jamservice.repository.JamRepository;
import com.jamconnect.jamservice.service.JamParticipantService;
import com.jamconnect.jamservice.service.UserReferenceResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JamParticipantServiceImpl implements JamParticipantService {

    private final JamRepository jamRepository;
    private final JamParticipantRepository jamParticipantRepository;
    private final UserReferenceResolver userReferenceResolver;
    private final ParticipantMapper participantMapper;
    private final JamEventProducer jamEventProducer;

    // ---------------------------------------------------------------
    // Join
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public ParticipantResponse joinJam(UUID jamId, UUID userId) {
        // Pessimistic write lock: two concurrent join requests for the
        // last open slot must not both read the same currentParticipantCount
        // and both decide there's room. This blocks the second request
        // until the first transaction commits/rolls back.
        Jam jam = jamRepository.findByIdForUpdate(jamId)
                .orElseThrow(() -> new JamNotFoundException(jamId));

        if (jam.getStatus() != JamStatus.UPCOMING) {
            throw new BusinessRuleException(
                    "Cannot join a jam that is " + jam.getStatus(), HttpStatus.CONFLICT);
        }

        UserReference user = userReferenceResolver.resolve(userId);

        Optional<JamParticipant> existingOpt = jamParticipantRepository.findByJamIdAndUserId(jamId, userId);

        JamParticipant participant;
        if (existingOpt.isPresent()) {
            participant = existingOpt.get();
            if (participant.getStatus() == ParticipantStatus.JOINED
                    || participant.getStatus() == ParticipantStatus.WAITLISTED) {
                throw new DuplicateParticipantException(jamId, userId);
            }
            // Previously LEFT or REMOVED — reactivate the same row rather
            // than inserting a new one (unique constraint is on jam_id+user_id
            // with no status component).
            participant.setLeftAt(null);
        } else {
            participant = JamParticipant.builder()
                    .jam(jam)
                    .user(user)
                    .build();
        }

        boolean hasOpenSlot = jam.getCurrentParticipantCount() < jam.getMaxCapacity();
        participant.setStatus(hasOpenSlot ? ParticipantStatus.JOINED : ParticipantStatus.WAITLISTED);

        JamParticipant saved = jamParticipantRepository.save(participant);

        if (hasOpenSlot) {
            jam.setCurrentParticipantCount(jam.getCurrentParticipantCount() + 1);
            jamRepository.save(jam);
        }

        log.info("User {} {} jam {}", userId, hasOpenSlot ? "joined" : "was waitlisted for", jamId);

        jamEventProducer.sendJamParticipantJoinedEvent(JamParticipantJoinedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jamId)
                .userId(userId)
                .status(saved.getStatus())
                .currentParticipantCount(jam.getCurrentParticipantCount())
                .joinedAt(saved.getJoinedAt())
                .build());

        return participantMapper.toResponse(saved);
    }

    // ---------------------------------------------------------------
    // Leave
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public void leaveJam(UUID jamId, UUID userId) {
        Jam jam = jamRepository.findByIdForUpdate(jamId)
                .orElseThrow(() -> new JamNotFoundException(jamId));

        JamParticipant participant = jamParticipantRepository.findByJamIdAndUserId(jamId, userId)
                .orElseThrow(() -> new ParticipantNotFoundException(jamId, userId));

        if (participant.getStatus() == ParticipantStatus.LEFT
                || participant.getStatus() == ParticipantStatus.REMOVED) {
            throw new BusinessRuleException("You have already left this jam", HttpStatus.CONFLICT);
        }

        boolean wasJoined = participant.getStatus() == ParticipantStatus.JOINED;

        participant.setStatus(ParticipantStatus.LEFT);
        participant.setLeftAt(LocalDateTime.now());
        jamParticipantRepository.save(participant);

        if (wasJoined) {
            jam.setCurrentParticipantCount(jam.getCurrentParticipantCount() - 1);
            promoteFromWaitlistIfAny(jam);
            jamRepository.save(jam);
        }

        log.info("User {} left jam {}", userId, jamId);

        jamEventProducer.sendJamParticipantLeftEvent(JamParticipantLeftEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jamId)
                .userId(userId)
                .currentParticipantCount(jam.getCurrentParticipantCount())
                .leftAt(participant.getLeftAt())
                .build());
    }

    /**
     * Promotes the oldest waitlisted participant into a JOINED slot.
     * Called only when a JOINED participant just freed up a slot, and
     * runs inside the same pessimistic-locked transaction as the leave,
     * so it can't race with a concurrent join for that same slot.
     */
    private void promoteFromWaitlistIfAny(Jam jam) {
        jamParticipantRepository
                .findFirstByJamIdAndStatusOrderByJoinedAtAsc(jam.getId(), ParticipantStatus.WAITLISTED)
                .ifPresent(promoted -> {
                    promoted.setStatus(ParticipantStatus.JOINED);
                    jamParticipantRepository.save(promoted);
                    jam.setCurrentParticipantCount(jam.getCurrentParticipantCount() + 1);

                    log.info("Promoted user {} from waitlist for jam {}",
                            promoted.getUser().getId(), jam.getId());

                    jamEventProducer.sendJamParticipantJoinedEvent(JamParticipantJoinedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .occurredAt(LocalDateTime.now())
                            .jamId(jam.getId())
                            .userId(promoted.getUser().getId())
                            .status(ParticipantStatus.JOINED)
                            .currentParticipantCount(jam.getCurrentParticipantCount())
                            .joinedAt(promoted.getJoinedAt())
                            .build());
                });
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    @Override
    public Page<ParticipantResponse> listParticipants(UUID jamId, ParticipantStatus status, Pageable pageable) {
        return jamParticipantRepository.findByJamIdAndStatus(jamId, status, pageable)
                .map(participantMapper::toResponse);
    }

    @Override
    public Page<ParticipantResponse> listJamsJoinedByUser(UUID userId, Pageable pageable) {
        return jamParticipantRepository.findByUserId(userId, pageable)
                .map(participantMapper::toResponse);
    }
}
