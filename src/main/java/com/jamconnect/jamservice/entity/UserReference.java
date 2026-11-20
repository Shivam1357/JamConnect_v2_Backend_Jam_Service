package com.jamconnect.jamservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Local, minimal read-model copy of a User Service user.
 * Populated and kept in sync via Kafka events (UserRegisteredEvent, UserUpdatedEvent, UserDeletedEvent).
 * NOT a source of truth — only used for display purposes (name, avatar) inside Jam Service.
 */
@Entity
@Table(name = "user_reference")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReference {

    @Id
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}