package com.jamconnect.jamservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A review of the jam session itself (organization, vibe, venue) —
 * not a review of a specific person. See JamParticipantReview for
 * peer-to-peer reviews between participants.
 */
@Entity
@Table(name = "jam_reviews",
        indexes = {
                @Index(name = "idx_jam_review_jam_id", columnList = "jam_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_jam_reviewer", columnNames = {"jam_id", "reviewer_id"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JamReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jam_id", nullable = false)
    private Jam jam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private UserReference reviewer;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}