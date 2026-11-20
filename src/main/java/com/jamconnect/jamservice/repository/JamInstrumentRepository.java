package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.JamInstrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JamInstrumentRepository extends JpaRepository<JamInstrument, UUID> {

    List<JamInstrument> findByJamId(UUID jamId);

    List<JamInstrument> findByInstrumentId(UUID instrumentId);

    void deleteByJamId(UUID jamId);
}