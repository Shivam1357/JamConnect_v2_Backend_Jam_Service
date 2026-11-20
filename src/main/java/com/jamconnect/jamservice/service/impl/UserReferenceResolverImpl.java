package com.jamconnect.jamservice.service.impl;

import com.jamconnect.jamservice.entity.UserReference;
import com.jamconnect.jamservice.exception.UserReferenceNotFoundException;
import com.jamconnect.jamservice.repository.UserReferenceRepository;
import com.jamconnect.jamservice.service.UserReferenceResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Currently only reads the local Kafka-synced table.
 *
 * TODO: if the row isn't found here (event lag, or a rare missed
 * event), the correct fallback is a one-off synchronous call to
 * User Service's internal lookup endpoint, then upsert the result
 * here before returning (the "lazy read-repair" pattern) — not
 * implemented yet since that internal endpoint/client isn't built.
 * Until then, a missing row surfaces as UserReferenceNotFoundException.
 */
@Component
@RequiredArgsConstructor
public class UserReferenceResolverImpl implements UserReferenceResolver {

    private final UserReferenceRepository userReferenceRepository;

    @Override
    public UserReference resolve(UUID userId) {
        return userReferenceRepository.findById(userId)
                .orElseThrow(() -> new UserReferenceNotFoundException(userId));
    }
}