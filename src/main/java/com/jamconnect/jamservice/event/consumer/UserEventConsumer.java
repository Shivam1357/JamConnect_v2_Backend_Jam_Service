package com.jamconnect.jamservice.event.consumer;

import com.jamconnect.jamservice.entity.UserReference;
import com.jamconnect.jamservice.event.payload.incoming.UserDeletedEvent;
import com.jamconnect.jamservice.event.payload.incoming.UserRegisteredEvent;
import com.jamconnect.jamservice.event.payload.incoming.UserUpdatedEvent;
import com.jamconnect.jamservice.repository.UserReferenceRepository;
import com.jamconnect.jamservice.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes user-related events from Constants.USER_TOPIC and keeps the
 * local user_reference table in sync. This is Jam Service's ONLY
 * dependency on User Service — everything else (jam CRUD, capacity,
 * reviews) works purely off this local copy.
 *
 * Multiple event types share USER_TOPIC, so this is a class-level
 * @KafkaListener with one @KafkaHandler per payload type; Spring Kafka
 * dispatches based on the deserialized object's runtime type. This
 * requires the consumer's JsonDeserializer to be configured with
 * TYPE_MAPPINGS pointing User Service's producer type-id headers at
 * these local classes (see KafkaConsumerConfig).
 */
@Component
@KafkaListener(topics = Constants.USER_TOPIC, groupId = Constants.JAM_SERVICE_CONSUMER_GROUP)
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserReferenceRepository userReferenceRepository;

    @KafkaHandler
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for userId={}", event.getUserId());

        UserReference reference = UserReference.builder()
                .id(event.getUserId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .profilePictureUrl(event.getProfilePictureUrl())
                .deleted(false)
                .build();

        userReferenceRepository.save(reference);
    }

    @KafkaHandler
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("Received UserUpdatedEvent for userId={}, changedFields={}",
                event.getUserId(), event.getChangedFields());

        UserReference reference = userReferenceRepository.findById(event.getUserId())
                .orElseGet(() -> UserReference.builder().id(event.getUserId()).build());

        reference.setFirstName(event.getFirstName());
        reference.setLastName(event.getLastName());
        reference.setProfilePictureUrl(event.getProfilePictureUrl());
        reference.setDeleted(false);

        userReferenceRepository.save(reference);
    }

    @KafkaHandler
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Received UserDeletedEvent for userId={}, hardDelete={}",
                event.getUserId(), event.isHardDelete());

        userReferenceRepository.findById(event.getUserId()).ifPresent(reference -> {
            // Soft delete only — jams/reviews already created still need
            // a display name; we never hard-delete this row so historical
            // jams don't end up with a dangling FK.
            reference.setFirstName("Deleted");
            reference.setLastName("User");
            reference.setProfilePictureUrl(null);
            reference.setDeleted(true);
            userReferenceRepository.save(reference);
        });
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("Received unrecognized event type on {}: {}", Constants.USER_TOPIC, event);
    }
}
