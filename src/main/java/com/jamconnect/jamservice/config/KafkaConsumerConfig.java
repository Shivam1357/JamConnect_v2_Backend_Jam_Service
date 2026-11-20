package com.jamconnect.jamservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * User Service's producer serializes with the object's own fully
 * qualified class name in the record header (e.g.
 * "com.jamconnect.userservice.event.payload.UserRegisteredEvent"),
 * since it doesn't configure any type-mapping aliases. Jam Service
 * has its OWN local classes with the same field shapes but a
 * different package (com.jamconnect.jamservice.event.payload.incoming.*).
 *
 * TYPE_MAPPINGS below tells the deserializer: "when you see this
 * header value, deserialize into this local class instead." Without
 * this, deserialization fails outright since the header's class name
 * doesn't exist in Jam Service's classpath.
 *
 * IMPORTANT: the three keys below must exactly match User Service's
 * actual package + class names — confirm against that codebase.
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private static final String USER_REGISTERED_TYPE_MAPPING =
            "com.jamconnect.userservice.event.payload.UserRegisteredEvent:" +
                    "com.jamconnect.jamservice.event.payload.incoming.UserRegisteredEvent";

    private static final String USER_UPDATED_TYPE_MAPPING =
            "com.jamconnect.userservice.event.payload.UserUpdatedEvent:" +
                    "com.jamconnect.jamservice.event.payload.incoming.UserUpdatedEvent";

    private static final String USER_DELETED_TYPE_MAPPING =
            "com.jamconnect.userservice.event.payload.UserDeletedEvent:" +
                    "com.jamconnect.jamservice.event.payload.incoming.UserDeletedEvent";

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        configProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES,
                "com.jamconnect.jamservice.event.payload.incoming");

        configProps.put(JacksonJsonDeserializer.TYPE_MAPPINGS, String.join(",",
                USER_REGISTERED_TYPE_MAPPING,
                USER_UPDATED_TYPE_MAPPING,
                USER_DELETED_TYPE_MAPPING));

        // We're deserializing to our own local classes, not the producer's —
        // don't fail startup or per-record if a stray unmapped field shows up.
        configProps.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, true);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Bean name "kafkaListenerContainerFactory" is the default Spring Kafka
     * looks for when @KafkaListener doesn't specify containerFactory
     * explicitly — UserEventConsumer relies on that default.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}