package com.jamconnect.jamservice.event.producer;

import com.jamconnect.jamservice.event.payload.outgoing.*;
import com.jamconnect.jamservice.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class JamEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendJamCreatedEvent(JamCreatedEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamCreatedEvent");
    }

    public void sendJamUpdatedEvent(JamUpdatedEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamUpdatedEvent");
    }

    public void sendJamLocationUpdatedEvent(JamLocationUpdatedEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamLocationUpdatedEvent");
    }

    public void sendJamDeletedEvent(JamDeletedEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamDeletedEvent");
    }

    public void sendJamParticipantJoinedEvent(JamParticipantJoinedEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamParticipantJoinedEvent");
    }

    public void sendJamParticipantLeftEvent(JamParticipantLeftEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamParticipantLeftEvent");
    }

    public void sendJamReviewSubmittedEvent(JamReviewSubmittedEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamReviewSubmittedEvent");
    }

    public void sendJamParticipantReviewSubmittedEvent(JamParticipantReviewSubmittedEvent event) {
        publish(Constants.JAM_TOPIC, event.getJamId().toString(), event, "JamParticipantReviewSubmittedEvent");
    }

    private void publish(String topic, String key, Object event, String eventType) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("{} published successfully to {} with offset: {}", eventType, topic,
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish {} for key {}: {}", eventType, key, ex.getMessage(), ex);
            }
        });
    }
}
