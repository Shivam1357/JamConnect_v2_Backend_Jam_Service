package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.UserReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserReferenceRepository extends JpaRepository<UserReference, UUID> {
}