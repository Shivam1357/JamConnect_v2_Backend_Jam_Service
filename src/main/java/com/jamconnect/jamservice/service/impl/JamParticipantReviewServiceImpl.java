package com.jamconnect.jamservice.service.impl;

import com.jamconnect.jamservice.dto.request.CreateParticipantReviewRequest;
import com.jamconnect.jamservice.dto.response.ParticipantReviewResponse;
import com.jamconnect.jamservice.entity.Jam;
import com.jamconnect.jamservice.entity.JamParticipantReview;
import com.jamconnect.jamservice.entity.UserReference;
import com.jamconnect.jamservice.enums.JamStatus;
import com.jamconnect.jamservice.event.payload.outgoing.JamParticipantReviewSubmittedEvent;
import com.jamconnect.jamservice.event.producer.JamEventProducer;
import com.jamconnect.jamservice.exception.InvalidReviewException;
import com.jamconnect.jamservice.exception.JamNotFoundException;
import com.jamconnect.jamservice.mapper.JamParticipantReviewMapper;
import com.jamconnect.jamservice.repository.JamParticipantRepository;
import com.jamconnect.jamservice.repository.JamParticipantReviewRepository;
import com.jamconnect.jamservice.repository.JamRepository;
import com.jamconnect.jamservice.service.JamParticipantReviewService;
import com.jamconnect.jamservice.service.UserReferenceResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JamParticipantReviewServiceImpl implements JamParticipantReviewService {

    private final JamRepository jamRepository;
    private final JamParticipantRepository jamParticipantRepository;
    private final JamParticipantReviewRepository jamParticipantReviewRepository;
    private final UserReferenceResolver userReferenceResolver;
    private final JamParticipantReviewMapper jamParticipantReviewMapper;
    private final JamEventProducer jamEventProducer;

    @Transactional
    @Override
    public ParticipantReviewResponse submitReview(UUID jamId, UUID reviewerId, CreateParticipantReviewRequest request) {
        Jam jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException(jamId));
        UUID reviewedUserId = request.getReviewedUserId();

        if (jam.getStatus() != JamStatus.COMPLETED) {
            throw new InvalidReviewException("You can only review participants after the jam has completed");
        }
        if (reviewerId.equals(reviewedUserId)) {
            throw new InvalidReviewException("You cannot review yourself");
        }
        if (!wasJamMember(jam, reviewerId) || !wasJamMember(jam, reviewedUserId)) {
            throw new InvalidReviewException("Both users must have been part of this jam");
        }
        if (jamParticipantReviewRepository.existsByJamIdAndReviewerIdAndReviewedUserId(jamId, reviewerId, reviewedUserId)) {
            throw new InvalidReviewException("You have already reviewed this participant for this jam");
        }

        UserReference reviewer = userReferenceResolver.resolve(reviewerId);
        UserReference reviewedUser = userReferenceResolver.resolve(reviewedUserId);

        JamParticipantReview review = JamParticipantReview.builder()
                .jam(jam)
                .reviewer(reviewer)
                .reviewedUser(reviewedUser)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        JamParticipantReview saved = jamParticipantReviewRepository.save(review);
        log.info("Participant review submitted: jamId={}, reviewerId={}, reviewedUserId={}, rating={}",
                jamId, reviewerId, reviewedUserId, request.getRating());

        jamEventProducer.sendJamParticipantReviewSubmittedEvent(JamParticipantReviewSubmittedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jamId)
                .reviewerId(reviewerId)
                .reviewedUserId(reviewedUserId)
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build());

        return jamParticipantReviewMapper.toResponse(saved);
    }

    /** A user "was a member" if they organized the jam or have any participant record (any status). */
    private boolean wasJamMember(Jam jam, UUID userId) {
        return jam.getOrganizer().getId().equals(userId)
                || jamParticipantRepository.existsByJamIdAndUserId(jam.getId(), userId);
    }

    @Override
    public Page<ParticipantReviewResponse> getReviewsForUser(UUID userId, Pageable pageable) {
        return jamParticipantReviewRepository.findByReviewedUserId(userId, pageable)
                .map(jamParticipantReviewMapper::toResponse);
    }

    @Override
    public Page<ParticipantReviewResponse> getReviewsForJam(UUID jamId, Pageable pageable) {
        return jamParticipantReviewRepository.findByJamId(jamId, pageable)
                .map(jamParticipantReviewMapper::toResponse);
    }

    @Override
    public Double getAverageRatingForUser(UUID userId) {
        return jamParticipantReviewRepository.findAverageRatingByReviewedUserId(userId);
    }
}
