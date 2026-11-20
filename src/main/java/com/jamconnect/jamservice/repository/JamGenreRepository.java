package com.jamconnect.jamservice.repository;

import com.jamconnect.jamservice.entity.JamGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JamGenreRepository extends JpaRepository<JamGenre, UUID> {

    List<JamGenre> findByJamId(UUID jamId);

    List<JamGenre> findByGenreId(UUID genreId);

    void deleteByJamId(UUID jamId);
}