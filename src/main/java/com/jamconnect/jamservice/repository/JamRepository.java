package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.Jam;
import com.jamconnect.jamservice.enums.JamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JamRepository extends JpaRepository<Jam, UUID> {

    Page<Jam> findByOrganizerId(UUID organizerId, Pageable pageable);

    Page<Jam> findByStatus(JamStatus status, Pageable pageable);

    List<Jam> findByStatusAndStartTimeBefore(JamStatus status, LocalDateTime time);

    Page<Jam> findByStatusAndStartTimeAfter(JamStatus status, LocalDateTime time, Pageable pageable);
}