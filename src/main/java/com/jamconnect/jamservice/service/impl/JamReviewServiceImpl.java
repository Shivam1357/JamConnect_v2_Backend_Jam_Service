package com.jamconnect.jamservice.service.impl;

import com.jamconnect.jamservice.dto.request.CreateJamReviewRequest;
import com.jamconnect.jamservice.dto.response.JamReviewResponse;
import com.jamconnect.jamservice.entity.Jam;
import com.jamconnect.jamservice.entity.JamReview;
import com.jamconnect.jamservice.entity.UserReference;
import com.jamconnect.jamservice.enums.JamStatus;
import com.jamconnect.jamservice.event.payload.outgoing.JamReviewSubmittedEvent;
import com.jamconnect.jamservice.event.producer.JamEventProducer;
import com.jamconnect.jamservice.exception.InvalidReviewException;
import com.jamconnect.jamservice.exception.JamNotFoundException;
import com.jamconnect.jamservice.mapper.JamReviewMapper;
import com.jamconnect.jamservice.repository.JamRepository;
import com.jamconnect.jamservice.repository.JamReviewRepository;
import com.jamconnect.jamservice.service.JamReviewService;
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
public class JamReviewServiceImpl implements JamReviewService {

    private final JamRepository jamRepository;
    private final JamReviewRepository jamReviewRepository;
    private final UserReferenceResolver userReferenceResolver;
    private final JamReviewMapper jamReviewMapper;
    private final JamEventProducer jamEventProducer;

    @Transactional
    @Override
    public JamReviewResponse submitReview(UUID jamId, UUID reviewerId, CreateJamReviewRequest request) {
        Jam jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException(jamId));

        if (jam.getStatus() != JamStatus.COMPLETED) {
            throw new InvalidReviewException("You can only review a jam after it has completed");
        }

        if (jamReviewRepository.existsByJamIdAndReviewerId(jamId, reviewerId)) {
            throw new InvalidReviewException("You have already reviewed this jam");
        }

        UserReference reviewer = userReferenceResolver.resolve(reviewerId);

        JamReview review = JamReview.builder()
                .jam(jam)
                .reviewer(reviewer)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        JamReview saved = jamReviewRepository.save(review);
        log.info("Jam review submitted: jamId={}, reviewerId={}, rating={}", jamId, reviewerId, request.getRating());

        jamEventProducer.sendJamReviewSubmittedEvent(JamReviewSubmittedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jamId)
                .reviewerId(reviewerId)
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build());

        return jamReviewMapper.toResponse(saved);
    }

    @Override
    public Page<JamReviewResponse> getReviewsForJam(UUID jamId, Pageable pageable) {
        return jamReviewRepository.findByJamId(jamId, pageable).map(jamReviewMapper::toResponse);
    }

    @Override
    public Double getAverageRating(UUID jamId) {
        return jamReviewRepository.findAverageRatingByJamId(jamId);
    }
}
