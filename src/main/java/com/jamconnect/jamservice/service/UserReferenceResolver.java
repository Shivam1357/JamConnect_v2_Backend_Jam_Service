package com.jamconnect.jamservice.service;

import com.jamconnect.jamservice.entity.UserReference;

import java.util.UUID;

/**
 * Single contract every service goes through to resolve a UUID into a
 * UserReference. Kept as its own interface (rather than folded into
 * JamService) since it's a cross-cutting dependency used by all four
 * service impls, not something specific to jam CRUD itself.
 */
public interface UserReferenceResolver {

    UserReference resolve(UUID userId);
}