package com.jamconnect.jamservice.entity;

import com.jamconnect.jamservice.enums.JamStatus;
import com.jamconnect.jamservice.enums.SkillLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "jams", indexes = {
        @Index(name = "idx_jam_organizer_id", columnList = "organizer_id"),
        @Index(name = "idx_jam_status", columnList = "status"),
        @Index(name = "idx_jam_start_time", columnList = "start_time")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private UserReference organizer;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false)
    private SkillLevel skillLevel;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "current_participant_count", nullable = false)
    @Builder.Default
    private Integer currentParticipantCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JamStatus status = JamStatus.UPCOMING;

    @OneToOne(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private JamAddress address;

    @Builder.Default
    @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<JamGenre> genres = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<JamInstrument> instruments = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<JamParticipant> participants = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Optimistic locking - helps guard capacity updates under concurrent joins. */
    @Version
    @Column(name = "version")
    private Long version;
}